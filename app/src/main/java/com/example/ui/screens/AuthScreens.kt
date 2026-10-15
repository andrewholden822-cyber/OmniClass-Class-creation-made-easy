package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AuthScreenMode

@Composable
fun AuthScreen(
    currentMode: AuthScreenMode,
    onModeChange: (AuthScreenMode) -> Unit,
    onLogin: (String, String) -> Unit,
    onSignUp: (String, String, String, String) -> Unit,
    onForgotPassword: (String, String) -> Unit,
    onDemoLogin: (String) -> Unit,
    onContinueAsGuest: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.surface
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Portal Logo & Hero
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.School,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(36.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "OmniClass Web Portal",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "The online learning platform where AI creates any class.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(28.dp))

            // Main Auth Card
            Card(
                modifier = Modifier
                    .widthIn(max = 480.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    when (currentMode) {
                        AuthScreenMode.LOGIN -> {
                            LoginForm(
                                onLogin = onLogin,
                                onForgotPassword = { onModeChange(AuthScreenMode.FORGOT_PASSWORD) },
                                onSwitchToSignUp = { onModeChange(AuthScreenMode.SIGNUP) }
                            )
                        }
                        AuthScreenMode.SIGNUP -> {
                            SignUpForm(
                                onSignUp = onSignUp,
                                onSwitchToLogin = { onModeChange(AuthScreenMode.LOGIN) }
                            )
                        }
                        AuthScreenMode.FORGOT_PASSWORD -> {
                            ForgotPasswordForm(
                                onSubmitReset = onForgotPassword,
                                onBackToLogin = { onModeChange(AuthScreenMode.LOGIN) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    Spacer(modifier = Modifier.height(16.dp))

                    // Demo & Guest Logins for instant evaluation
                    Text(
                        text = "Quick Demo Access (One-Tap):",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = { onDemoLogin("student") },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("demo_student_button"),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Student Mode")
                        }
                        OutlinedButton(
                            onClick = { onDemoLogin("instructor") },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("demo_instructor_button"),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Instructor Mode")
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    TextButton(
                        onClick = onContinueAsGuest,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("guest_tour_button")
                    ) {
                        Text("Explore as Guest →", color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
    }
}

@Composable
private fun LoginForm(
    onLogin: (String, String) -> Unit,
    onForgotPassword: () -> Unit,
    onSwitchToSignUp: () -> Unit
) {
    var email by remember { mutableStateOf("alex.rivera@omniclass.edu") }
    var password by remember { mutableStateOf("password123") }
    var passwordVisible by remember { mutableStateOf(false) }
    var rememberMe by remember { mutableStateOf(true) }

    Text(
        text = "Sign In to Your Account",
        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.onSurface
    )
    Text(
        text = "Access your classes, syllabus, assignments, and AI tutor.",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    Spacer(modifier = Modifier.height(18.dp))

    OutlinedTextField(
        value = email,
        onValueChange = { email = it },
        label = { Text("Email Address") },
        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("login_email_input"),
        shape = RoundedCornerShape(10.dp)
    )

    Spacer(modifier = Modifier.height(12.dp))

    OutlinedTextField(
        value = password,
        onValueChange = { password = it },
        label = { Text("Password") },
        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
        trailingIcon = {
            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                Icon(
                    imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                    contentDescription = if (passwordVisible) "Hide password" else "Show password"
                )
            }
        },
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("login_password_input"),
        shape = RoundedCornerShape(10.dp)
    )

    Spacer(modifier = Modifier.height(8.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = rememberMe,
                onCheckedChange = { rememberMe = it },
                colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier.size(36.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text("Remember me", style = MaterialTheme.typography.bodySmall)
        }

        Text(
            text = "Forgot password?",
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .clickable { onForgotPassword() }
                .testTag("forgot_password_link")
        )
    }

    Spacer(modifier = Modifier.height(18.dp))

    Button(
        onClick = { onLogin(email, password) },
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .testTag("login_submit_button"),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
    ) {
        Text("Sign In to Portal", fontWeight = FontWeight.Bold)
    }

    Spacer(modifier = Modifier.height(14.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Don't have an account? ", style = MaterialTheme.typography.bodySmall)
        Text(
            text = "Sign Up",
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .clickable { onSwitchToSignUp() }
                .testTag("switch_to_signup_link")
        )
    }
}

@Composable
private fun SignUpForm(
    onSignUp: (String, String, String, String) -> Unit,
    onSwitchToLogin: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("student") } // "student" or "instructor"
    var passwordVisible by remember { mutableStateOf(false) }

    Text(
        text = "Create an Account",
        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.onSurface
    )
    Text(
        text = "Join OmniClass to create and master any subject with AI.",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    Spacer(modifier = Modifier.height(18.dp))

    OutlinedTextField(
        value = name,
        onValueChange = { name = it },
        label = { Text("Full Name") },
        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("signup_name_input"),
        shape = RoundedCornerShape(10.dp)
    )

    Spacer(modifier = Modifier.height(12.dp))

    OutlinedTextField(
        value = email,
        onValueChange = { email = it },
        label = { Text("Email Address") },
        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("signup_email_input"),
        shape = RoundedCornerShape(10.dp)
    )

    Spacer(modifier = Modifier.height(12.dp))

    OutlinedTextField(
        value = password,
        onValueChange = { password = it },
        label = { Text("Password") },
        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
        trailingIcon = {
            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                Icon(
                    imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                    contentDescription = null
                )
            }
        },
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("signup_password_input"),
        shape = RoundedCornerShape(10.dp)
    )

    Spacer(modifier = Modifier.height(16.dp))

    Text("I am joining as a:", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
    Spacer(modifier = Modifier.height(6.dp))
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Surface(
            shape = RoundedCornerShape(10.dp),
            color = if (role == "student") MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier
                .weight(1f)
                .clickable { role = "student" }
                .border(
                    width = if (role == "student") 2.dp else 0.dp,
                    color = if (role == "student") MaterialTheme.colorScheme.primary else Color.Transparent,
                    shape = RoundedCornerShape(10.dp)
                )
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("Student Learner", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                Text("Enroll & study courses", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        Surface(
            shape = RoundedCornerShape(10.dp),
            color = if (role == "instructor") MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier
                .weight(1f)
                .clickable { role = "instructor" }
                .border(
                    width = if (role == "instructor") 2.dp else 0.dp,
                    color = if (role == "instructor") MaterialTheme.colorScheme.primary else Color.Transparent,
                    shape = RoundedCornerShape(10.dp)
                )
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("Course Creator", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                Text("Design & publish classes", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }

    Spacer(modifier = Modifier.height(20.dp))

    Button(
        onClick = {
            if (name.isNotBlank() && email.isNotBlank() && password.isNotBlank()) {
                onSignUp(name, email, password, role)
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .testTag("signup_submit_button"),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
    ) {
        Text("Create Free Account", fontWeight = FontWeight.Bold)
    }

    Spacer(modifier = Modifier.height(14.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Already have an account? ", style = MaterialTheme.typography.bodySmall)
        Text(
            text = "Log In",
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .clickable { onSwitchToLogin() }
                .testTag("switch_to_login_link")
        )
    }
}

@Composable
private fun ForgotPasswordForm(
    onSubmitReset: (String, String) -> Unit,
    onBackToLogin: () -> Unit
) {
    var step by remember { mutableStateOf(1) } // 1: Email, 2: Code, 3: New Pass
    var email by remember { mutableStateOf("") }
    var code by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var simulatedCode by remember { mutableStateOf("729401") }

    Text(
        text = "Reset Your Password",
        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.onSurface
    )
    Text(
        text = when (step) {
            1 -> "Enter your email address to receive a secure recovery code."
            2 -> "Enter the 6-digit verification code sent to your email."
            else -> "Choose a strong new password for your account."
        },
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    Spacer(modifier = Modifier.height(18.dp))

    when (step) {
        1 -> {
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Account Email") },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("forgot_email_input"),
                shape = RoundedCornerShape(10.dp)
            )

            Spacer(modifier = Modifier.height(18.dp))

            Button(
                onClick = {
                    if (email.isNotBlank()) {
                        step = 2
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("send_recovery_code_button"),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Send Verification Code", fontWeight = FontWeight.Bold)
            }
        }
        2 -> {
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Key, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Simulated Code: $simulatedCode (Tap to fill)",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.clickable { code = simulatedCode }
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = code,
                onValueChange = { code = it },
                label = { Text("6-Digit Code") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("verification_code_input"),
                shape = RoundedCornerShape(10.dp)
            )

            Spacer(modifier = Modifier.height(18.dp))

            Button(
                onClick = {
                    if (code.isNotBlank()) step = 3
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("verify_code_button"),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Verify Code", fontWeight = FontWeight.Bold)
            }
        }
        3 -> {
            OutlinedTextField(
                value = newPassword,
                onValueChange = { newPassword = it },
                label = { Text("New Password") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("new_password_input"),
                shape = RoundedCornerShape(10.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirm New Password") },
                leadingIcon = { Icon(Icons.Default.Check, contentDescription = null) },
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("confirm_password_input"),
                shape = RoundedCornerShape(10.dp)
            )

            Spacer(modifier = Modifier.height(18.dp))

            Button(
                onClick = {
                    if (newPassword.isNotBlank() && newPassword == confirmPassword) {
                        onSubmitReset(email.ifBlank { "user@omniclass.edu" }, newPassword)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("reset_password_submit_button"),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Save New Password", fontWeight = FontWeight.Bold)
            }
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    TextButton(
        onClick = onBackToLogin,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("← Back to Sign In", color = MaterialTheme.colorScheme.primary)
    }
}
