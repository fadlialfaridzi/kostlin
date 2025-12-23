package com.example.kostlin.data.mapper

import com.example.kostlin.core.session.SessionManager
import com.example.kostlin.data.remote.dto.auth.AuthResponseDto
import com.example.kostlin.data.remote.dto.auth.TokenPairDto
import com.example.kostlin.data.remote.dto.auth.UserDto
import com.example.kostlin.domain.model.User

fun AuthResponseDto.toDomainUser(): User {
    // Set SessionManager with user data for owner checking
    SessionManager.setUser(
        id = user.id,
        name = user.fullName,
        email = user.email
    )
    return user.toDomain(tokens)
}

fun UserDto.toDomain(tokens: TokenPairDto? = null): User = User(
    id = id.toString(),
    fullName = fullName,
    email = email,
    phoneNumber = phoneNumber,
    token = tokens?.accessToken,
    refreshToken = tokens?.refreshToken
)

