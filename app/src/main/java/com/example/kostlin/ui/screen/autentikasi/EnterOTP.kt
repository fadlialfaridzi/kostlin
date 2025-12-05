package com.example.kostlin.ui.screen.autentikasi

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.kostlin.ui.theme.ButtonBlue
import com.example.kostlin.ui.theme.DarkText
import com.example.kostlin.ui.theme.LightText
import com.example.kostlin.ui.theme.LinkColor

@Composable
fun EnterOTPScreen(
    email: String,
    onNavigateBack: () -> Unit,
    onNavigateToNewPassword: (String) -> Unit,
    onResendCode: (String) -> Unit
) {
    var otp1 by remember { mutableStateOf("") }
    var otp2 by remember { mutableStateOf("") }
    var otp3 by remember { mutableStateOf("") }
    var otp4 by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var focusedIndex by remember { mutableStateOf(0) }

    val focusRequesters = remember { List(4) { FocusRequester() } }

    LaunchedEffect(focusedIndex) {
        if (focusedIndex < 4) {
            focusRequesters[focusedIndex].requestFocus()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))
            
            // Back button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    modifier = Modifier.clickable { onNavigateBack() }
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Title
            Text(
                text = "Enter OTP",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = DarkText,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Instructions
            Text(
                text = "We have just sent you 4 digit code via your email",
                style = MaterialTheme.typography.bodyMedium,
                color = LightText,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = email,
                style = MaterialTheme.typography.bodyMedium,
                color = DarkText,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            // OTP Input Fields
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                for (i in 0..3) {
                    val otpValue = when (i) {
                        0 -> otp1
                        1 -> otp2
                        2 -> otp3
                        else -> otp4
                    }
                    val onValueChange: (String) -> Unit = { value ->
                        if (value.length <= 1) {
                            when (i) {
                                0 -> otp1 = value
                                1 -> otp2 = value
                                2 -> otp3 = value
                                else -> otp4 = value
                            }
                            if (value.isNotEmpty() && i < 3) {
                                focusedIndex = i + 1
                            }
                        }
                    }

                    OutlinedTextField(
                        value = otpValue,
                        onValueChange = onValueChange,
                        modifier = Modifier
                            .size(60.dp)
                            .focusRequester(focusRequesters[i]),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = if (otpValue.isNotEmpty()) Color(0xFFF5F5F5) else Color.White,
                            focusedContainerColor = Color.White,
                            unfocusedBorderColor = if (focusedIndex == i) ButtonBlue else Color(0xFFE0E0E0),
                            focusedBorderColor = ButtonBlue
                        ),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = if (i == 3) ImeAction.Done else ImeAction.Next
                        ),
                        textStyle = MaterialTheme.typography.headlineMedium.copy(
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold
                        ),
                        singleLine = true
                    )
                }
            }

            // Error message
            if (errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    color = Color.Red,
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Continue Button
            Button(
                onClick = {
                    val otp = otp1 + otp2 + otp3 + otp4
                    if (otp.length != 4) {
                        errorMessage = "Please enter complete OTP"
                    } else {
                        errorMessage = ""
                        onNavigateToNewPassword(email)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ButtonBlue)
            ) {
                Text(text = "Continue", color = Color.White, style = MaterialTheme.typography.bodyLarge)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Resend Code
            Row(horizontalArrangement = Arrangement.Center) {
                Text("Didn't receive code? ", color = LightText)
                Text(
                    text = "Resend Code",
                    color = LinkColor,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.clickable {
                        onResendCode(email)
                    }
                )
            }
        }
    }
}

