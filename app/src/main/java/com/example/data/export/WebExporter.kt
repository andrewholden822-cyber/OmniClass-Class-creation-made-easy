package com.example.data.export

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import com.example.data.models.ClassEntity
import com.example.data.models.FlashcardEntity
import com.example.data.models.ModuleEntity
import com.example.data.models.QuizQuestionEntity
import com.example.data.models.StudentRosterEntity
import org.json.JSONArray
import java.io.File

object WebExporter {

    fun generateCourseWebsiteHtml(
        course: ClassEntity,
        modules: List<ModuleEntity>,
        quizzes: List<QuizQuestionEntity>,
        flashcards: List<FlashcardEntity>,
        roster: List<StudentRosterEntity>
    ): String {
        val takeawaysList = mutableListOf<String>()
        try {
            val arr = JSONArray(course.keyTakeawaysJson)
            for (i in 0 until arr.length()) takeawaysList.add(arr.getString(i))
        } catch (_: Exception) {}

        val modulesHtml = modules.mapIndexed { idx, mod ->
            """
            <div class="module-card">
                <div class="module-header" onclick="toggleModule(${idx})">
                    <div class="module-title-row">
                        <span class="badge">Module ${mod.orderIndex}</span>
                        <h3>${escapeHtml(mod.title)}</h3>
                    </div>
                    <span class="duration">${mod.durationMinutes} mins</span>
                </div>
                <div id="module-content-${idx}" class="module-body ${if (idx == 0) "active" else ""}">
                    <p class="summary">${escapeHtml(mod.summary)}</p>
                    <div class="markdown-prose">
                        ${markdownToHtml(mod.lessonContentMarkdown)}
                    </div>
                </div>
            </div>
            """.trimIndent()
        }.joinToString("\n")

        val quizzesJson = JSONArray().apply {
            quizzes.forEach { q ->
                put(org.json.JSONObject().apply {
                    put("question", q.question)
                    put("options", JSONArray(q.optionsJson))
                    put("correct", q.correctIndex)
                    put("explanation", q.explanation)
                })
            }
        }.toString()

        val flashcardsHtml = flashcards.mapIndexed { idx, fc ->
            """
            <div class="flashcard" onclick="this.classList.toggle('flipped')">
                <div class="card-inner">
                    <div class="card-front">
                        <span class="card-badge">Card ${idx + 1}</span>
                        <h4>${escapeHtml(fc.frontTerm)}</h4>
                        <span class="hint">Click to flip</span>
                    </div>
                    <div class="card-back">
                        <p>${escapeHtml(fc.backDefinition)}</p>
                    </div>
                </div>
            </div>
            """.trimIndent()
        }.joinToString("\n")

        val rosterHtml = roster.map { st ->
            """
            <tr class="roster-row">
                <td><span class="avatar" style="background:${st.avatarColorHex}">${st.name.take(1)}</span> ${escapeHtml(st.name)}</td>
                <td>${escapeHtml(st.email)}</td>
                <td><span class="grade">${st.gradePercent}%</span></td>
                <td><span class="status-pill ${st.assignmentStatus.lowercase().replace(" ", "-")}">${st.assignmentStatus}</span></td>
            </tr>
            """.trimIndent()
        }.joinToString("\n")

        return """
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${escapeHtml(course.title)} - OmniClass Web Portal</title>
    <style>
        :root {
            --primary: #4f46e5;
            --primary-hover: #4338ca;
            --bg: #0f172a;
            --surface: #1e293b;
            --surface-elevated: #334155;
            --text: #f8fafc;
            --text-muted: #94a3b8;
            --border: #334155;
            --accent: #10b981;
            --gold: #f59e0b;
        }
        * { box-sizing: border-box; margin: 0; padding: 0; font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif; }
        body { background: var(--bg); color: var(--text); line-height: 1.6; }
        
        /* Top Navigation Bar */
        nav { background: rgba(15, 23, 42, 0.95); backdrop-filter: blur(12px); border-bottom: 1px solid var(--border); position: sticky; top: 0; z-index: 100; padding: 14px 24px; display: flex; justify-content: space-between; align-items: center; }
        .nav-logo { font-size: 1.25rem; font-weight: 800; color: #818cf8; display: flex; align-items: center; gap: 8px; text-decoration: none; }
        .nav-actions { display: flex; gap: 12px; align-items: center; }
        .btn { padding: 8px 18px; border-radius: 8px; font-weight: 600; cursor: pointer; text-decoration: none; border: none; font-size: 0.9rem; transition: all 0.2s; }
        .btn-outline { background: transparent; color: var(--text); border: 1px solid var(--border); }
        .btn-outline:hover { background: var(--surface); }
        .btn-primary { background: var(--primary); color: white; }
        .btn-primary:hover { background: var(--primary-hover); }

        /* Hero Banner */
        .hero { background: linear-gradient(135deg, ${course.bannerGradientStart}, ${course.bannerGradientEnd}); border-bottom: 1px solid var(--border); padding: 50px 24px; text-align: left; }
        .container { max-width: 1080px; margin: 0 auto; padding: 0 16px; }
        .badge { display: inline-block; background: rgba(255,255,255,0.15); color: #e0e7ff; padding: 4px 12px; border-radius: 9999px; font-size: 0.75rem; font-weight: 700; text-transform: uppercase; margin-bottom: 14px; }
        .hero h1 { font-size: 2.5rem; font-weight: 800; margin-bottom: 12px; line-height: 1.2; }
        .hero p.tagline { font-size: 1.2rem; color: #c7d2fe; margin-bottom: 24px; max-width: 800px; }
        .hero-meta { display: flex; flex-wrap: wrap; gap: 20px; align-items: center; font-size: 0.95rem; color: #e2e8f0; }
        
        /* Tabs */
        .tab-bar { display: flex; border-bottom: 1px solid var(--border); margin: 30px 0 20px 0; gap: 8px; overflow-x: auto; }
        .tab-btn { background: none; border: none; color: var(--text-muted); padding: 12px 20px; font-weight: 600; cursor: pointer; border-bottom: 2px solid transparent; font-size: 1rem; }
        .tab-btn.active { color: #818cf8; border-bottom: 2px solid #818cf8; }
        
        /* Section Panels */
        .tab-panel { display: none; }
        .tab-panel.active { display: block; }

        /* Modules & Lessons */
        .module-card { background: var(--surface); border: 1px solid var(--border); border-radius: 12px; margin-bottom: 16px; overflow: hidden; }
        .module-header { padding: 18px 24px; display: flex; justify-content: space-between; align-items: center; cursor: pointer; background: rgba(255,255,255,0.02); }
        .module-header:hover { background: rgba(255,255,255,0.05); }
        .module-body { padding: 24px; display: none; border-top: 1px solid var(--border); }
        .module-body.active { display: block; }
        .markdown-prose { color: #cbd5e1; font-size: 1rem; line-height: 1.7; margin-top: 16px; }
        .markdown-prose h1, .markdown-prose h2, .markdown-prose h3 { color: var(--text); margin: 16px 0 8px 0; }
        .markdown-prose code { background: #090d16; padding: 2px 6px; border-radius: 4px; font-family: monospace; color: #a5b4fc; }

        /* Quizzes */
        .quiz-box { background: var(--surface); border: 1px solid var(--border); padding: 24px; border-radius: 12px; margin-bottom: 20px; }
        .quiz-option { display: block; padding: 12px 16px; background: rgba(255,255,255,0.05); border: 1px solid var(--border); border-radius: 8px; margin: 10px 0; cursor: pointer; transition: all 0.2s; }
        .quiz-option:hover { background: rgba(99, 102, 241, 0.15); border-color: #6366f1; }
        .quiz-option.correct { background: rgba(16, 185, 129, 0.2); border-color: #10b981; }
        .quiz-option.wrong { background: rgba(239, 68, 68, 0.2); border-color: #ef4444; }

        /* Flashcards Grid */
        .flashcards-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(280px, 1fr)); gap: 20px; }
        .flashcard { perspective: 1000px; height: 180px; cursor: pointer; }
        .card-inner { position: relative; width: 100%; height: 100%; text-align: center; transition: transform 0.6s; transform-style: preserve-3d; }
        .flashcard.flipped .card-inner { transform: rotateY(180deg); }
        .card-front, .card-back { position: absolute; width: 100%; height: 100%; backface-visibility: hidden; border-radius: 12px; padding: 20px; display: flex; flex-direction: column; justify-content: center; align-items: center; border: 1px solid var(--border); }
        .card-front { background: var(--surface); }
        .card-back { background: #1e1b4b; color: #c7d2fe; transform: rotateY(180deg); font-size: 0.95rem; }

        /* Roster Table */
        table { width: 100%; border-collapse: collapse; background: var(--surface); border-radius: 12px; overflow: hidden; border: 1px solid var(--border); }
        th, td { padding: 14px 18px; text-align: left; border-bottom: 1px solid var(--border); }
        th { background: rgba(255,255,255,0.04); color: var(--text-muted); font-size: 0.85rem; text-transform: uppercase; }
        .avatar { display: inline-flex; align-items: center; justify-content: center; width: 28px; height: 28px; border-radius: 9999px; color: white; font-weight: bold; font-size: 0.8rem; margin-right: 8px; }
        .status-pill { padding: 4px 10px; border-radius: 9999px; font-size: 0.75rem; font-weight: 600; }
        .status-pill.graded { background: rgba(16,185,129,0.2); color: #10b981; }
        .status-pill.submitted { background: rgba(59,130,246,0.2); color: #60a5fa; }
        .status-pill.pending-review { background: rgba(245,158,11,0.2); color: #fbbf24; }

        /* Auth Modal */
        .modal-overlay { display: none; position: fixed; inset: 0; background: rgba(0,0,0,0.7); backdrop-filter: blur(4px); z-index: 200; justify-content: center; align-items: center; }
        .modal-overlay.active { display: flex; }
        .modal { background: var(--surface); border: 1px solid var(--border); padding: 32px; border-radius: 16px; width: 100%; max-width: 420px; }
        .modal h2 { margin-bottom: 8px; }
        .form-group { margin-top: 16px; }
        .form-group label { display: block; font-size: 0.85rem; color: var(--text-muted); margin-bottom: 6px; }
        .form-group input { width: 100%; padding: 10px 14px; background: #0f172a; border: 1px solid var(--border); border-radius: 8px; color: white; }
    </style>
</head>
<body>

    <nav>
        <a href="#" class="nav-logo">🎓 OmniClass Portal</a>
        <div class="nav-actions">
            <button class="btn btn-outline" onclick="openModal('login')">Log In</button>
            <button class="btn btn-primary" onclick="openModal('signup')">Sign Up Free</button>
        </div>
    </nav>

    <div class="hero">
        <div class="container">
            <span class="badge">${escapeHtml(course.domain)} &bull; ${escapeHtml(course.level)}</span>
            <h1>${escapeHtml(course.title)}</h1>
            <p class="tagline">${escapeHtml(course.tagline)}</p>
            <div class="hero-meta">
                <span>Instructor: <strong>${escapeHtml(course.instructorName)}</strong></span>
                <span>&bull;</span>
                <span>Duration: <strong>${escapeHtml(course.durationText)}</strong></span>
                <span>&bull;</span>
                <span>Rating: <strong>★ ${course.rating}</strong> (${course.studentCount} students)</span>
            </div>
        </div>
    </div>

    <div class="container" style="margin-top: 24px; margin-bottom: 60px;">
        <div class="tab-bar">
            <button class="tab-btn active" onclick="switchTab('syllabus', this)">Syllabus & Lessons (${modules.size})</button>
            <button class="tab-btn" onclick="switchTab('quizzes', this)">Knowledge Check (${quizzes.size})</button>
            <button class="tab-btn" onclick="switchTab('flashcards', this)">Flashcards (${flashcards.size})</button>
            <button class="tab-btn" onclick="switchTab('roster', this)">Classroom Roster</button>
        </div>

        <div id="syllabus" class="tab-panel active">
            ${modulesHtml}
        </div>

        <div id="quizzes" class="tab-panel">
            <div id="quiz-container"></div>
        </div>

        <div id="flashcards" class="tab-panel">
            <div class="flashcards-grid">
                ${flashcardsHtml}
            </div>
        </div>

        <div id="roster" class="tab-panel">
            <table>
                <thead>
                    <tr><th>Student</th><th>Email</th><th>Score</th><th>Status</th></tr>
                </thead>
                <tbody>
                    ${rosterHtml}
                </tbody>
            </table>
        </div>
    </div>

    <!-- Auth Modal -->
    <div id="authModal" class="modal-overlay">
        <div class="modal">
            <h2 id="modalTitle">Account Portal</h2>
            <p style="color:var(--text-muted); font-size:0.9rem;" id="modalSub">Log in to track your course progress.</p>
            <form onsubmit="event.preventDefault(); closeModal(); alert('Welcome to OmniClass!');">
                <div class="form-group">
                    <label>Email Address</label>
                    <input type="email" value="alex.rivera@omniclass.edu" required />
                </div>
                <div class="form-group">
                    <label>Password</label>
                    <input type="password" value="••••••••" required />
                </div>
                <button type="submit" class="btn btn-primary" style="width:100%; margin-top:20px; padding:12px;">Continue to Course</button>
                <button type="button" class="btn btn-outline" style="width:100%; margin-top:8px;" onclick="closeModal()">Cancel</button>
            </form>
        </div>
    </div>

    <script>
        function switchTab(tabId, el) {
            document.querySelectorAll('.tab-btn').forEach(b => b.classList.remove('active'));
            document.querySelectorAll('.tab-panel').forEach(p => p.classList.remove('active'));
            el.classList.add('active');
            document.getElementById(tabId).classList.add('active');
        }

        function toggleModule(idx) {
            const el = document.getElementById('module-content-' + idx);
            el.classList.toggle('active');
        }

        function openModal(mode) {
            document.getElementById('modalTitle').innerText = mode === 'signup' ? 'Create Student Account' : 'Welcome Back';
            document.getElementById('authModal').classList.add('active');
        }

        function closeModal() {
            document.getElementById('authModal').classList.remove('active');
        }

        // Render Quizzes
        const quizData = ${quizzesJson};
        const quizContainer = document.getElementById('quiz-container');
        quizData.forEach((q, idx) => {
            const box = document.createElement('div');
            box.className = 'quiz-box';
            let optionsHtml = '';
            q.options.forEach((opt, optIdx) => {
                optionsHtml += `<div class="quiz-option" onclick="checkAnswer(${'$'}{idx}, ${'$'}{optIdx}, this)">${'$'}{opt}</div>`;
            });
            box.innerHTML = `
                <h4>Question ${'$'}{idx + 1}: ${'$'}{q.question}</h4>
                <div style="margin-top: 12px;">${'$'}{optionsHtml}</div>
                <p id="explanation-${'$'}{idx}" style="display:none; margin-top:14px; color:#a5b4fc; font-size:0.9rem; border-left:3px solid #6366f1; padding-left:10px;">${'$'}{q.explanation}</p>
            `;
            quizContainer.appendChild(box);
        });

        function checkAnswer(qIdx, optIdx, el) {
            const q = quizData[qIdx];
            const parent = el.parentElement;
            if (parent.dataset.answered) return;
            parent.dataset.answered = 'true';
            if (optIdx === q.correct) {
                el.classList.add('correct');
            } else {
                el.classList.add('wrong');
                parent.children[q.correct].classList.add('correct');
            }
            document.getElementById('explanation-' + qIdx).style.display = 'block';
        }
    </script>
</body>
</html>
        """.trimIndent()
    }

    fun shareCourseAsWebsite(context: Context, htmlContent: String, courseTitle: String) {
        try {
            val fileName = "omniclass_${courseTitle.lowercase().replace(Regex("[^a-z0-9]"), "_")}.html"
            val exportDir = File(context.cacheDir, "exports")
            if (!exportDir.exists()) exportDir.mkdirs()
            val file = File(exportDir, fileName)
            file.writeText(htmlContent)

            val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/html"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, "$courseTitle - Interactive Web Course")
                putExtra(Intent.EXTRA_TEXT, "Here is the interactive online class website for '$courseTitle'. Open it in any web browser on iOS, Mac, Windows, or Android!")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(intent, "Export Online Course Website"))
        } catch (_: Exception) {
            // Fallback to text sharing
            val shareTextIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, "$courseTitle - Course Portal")
                putExtra(Intent.EXTRA_TEXT, htmlContent)
            }
            context.startActivity(Intent.createChooser(shareTextIntent, "Share Web Code"))
        }
    }

    private fun escapeHtml(text: String): String =
        text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;")

    private fun markdownToHtml(md: String): String {
        return md
            .replace(Regex("^# (.*)", RegexOption.MULTILINE), "<h2>$1</h2>")
            .replace(Regex("^## (.*)", RegexOption.MULTILINE), "<h3>$1</h3>")
            .replace(Regex("^### (.*)", RegexOption.MULTILINE), "<h4>$1</h4>")
            .replace(Regex("\\*\\*(.*?)\\*\\*"), "<strong>$1</strong>")
            .replace(Regex("`([^`]+)`"), "<code>$1</code>")
            .replace("\n\n", "<br><br>")
    }
}
