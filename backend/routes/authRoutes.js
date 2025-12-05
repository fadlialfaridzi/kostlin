const express = require('express');
const router = express.Router();
const authController = require('../controllers/authController');
const { authenticateToken } = require('../middleware/auth');
const {
    validateRegister,
    validateLogin,
    validateForgotPassword,
    validateOTP,
    validateResetPassword
} = require('../middleware/validation');

// Public routes
router.post('/register', validateRegister, authController.register);
router.post('/login', validateLogin, authController.login);
router.post('/forgot-password', validateForgotPassword, authController.forgotPassword);
router.post('/verify-otp', validateOTP, authController.verifyOTP);
router.post('/reset-password', validateResetPassword, authController.resetPassword);

// Protected routes
router.get('/profile', authenticateToken, authController.getProfile);

module.exports = router;

