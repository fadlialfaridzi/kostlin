const { body, validationResult } = require('express-validator');

const handleValidationErrors = (req, res, next) => {
    const errors = validationResult(req);
    if (!errors.isEmpty()) {
        return res.status(400).json({
            success: false,
            message: 'Validation failed',
            errors: errors.array()
        });
    }
    next();
};

// Register validation
const validateRegister = [
    body('fullName')
        .trim()
        .notEmpty().withMessage('Full name is required')
        .isLength({ min: 2 }).withMessage('Full name must be at least 2 characters'),
    body('email')
        .trim()
        .notEmpty().withMessage('Email is required')
        .isEmail().withMessage('Invalid email format'),
    body('password')
        .notEmpty().withMessage('Password is required')
        .isLength({ min: 6 }).withMessage('Password must be at least 6 characters'),
    handleValidationErrors
];

// Login validation
const validateLogin = [
    body('email')
        .trim()
        .notEmpty().withMessage('Email is required')
        .isEmail().withMessage('Invalid email format'),
    body('password')
        .notEmpty().withMessage('Password is required'),
    handleValidationErrors
];

// Forgot password validation
const validateForgotPassword = [
    body('email')
        .trim()
        .notEmpty().withMessage('Email is required')
        .isEmail().withMessage('Invalid email format'),
    handleValidationErrors
];

// OTP validation
const validateOTP = [
    body('email')
        .trim()
        .notEmpty().withMessage('Email is required')
        .isEmail().withMessage('Invalid email format'),
    body('code')
        .trim()
        .notEmpty().withMessage('OTP code is required')
        .isLength({ min: 6, max: 6 }).withMessage('OTP code must be 6 digits'),
    handleValidationErrors
];

// Reset password validation
const validateResetPassword = [
    body('email')
        .trim()
        .notEmpty().withMessage('Email is required')
        .isEmail().withMessage('Invalid email format'),
    body('password')
        .notEmpty().withMessage('Password is required')
        .isLength({ min: 6 }).withMessage('Password must be at least 6 characters'),
    handleValidationErrors
];

// Booking validation
const validateBooking = [
    body('kosId')
        .notEmpty().withMessage('Kos ID is required')
        .isInt().withMessage('Kos ID must be a number'),
    body('checkInDate')
        .notEmpty().withMessage('Check-in date is required')
        .isISO8601().withMessage('Invalid date format'),
    body('checkOutDate')
        .notEmpty().withMessage('Check-out date is required')
        .isISO8601().withMessage('Invalid date format'),
    body('capacity')
        .optional()
        .isInt({ min: 1 }).withMessage('Capacity must be at least 1'),
    handleValidationErrors
];

// Review validation
const validateReview = [
    body('kosId')
        .notEmpty().withMessage('Kos ID is required')
        .isInt().withMessage('Kos ID must be a number'),
    body('rating')
        .notEmpty().withMessage('Rating is required')
        .isFloat({ min: 0, max: 5 }).withMessage('Rating must be between 0 and 5'),
    body('comment')
        .optional()
        .trim()
        .isLength({ max: 1000 }).withMessage('Comment must be less than 1000 characters'),
    handleValidationErrors
];

module.exports = {
    validateRegister,
    validateLogin,
    validateForgotPassword,
    validateOTP,
    validateResetPassword,
    validateBooking,
    validateReview,
    handleValidationErrors
};

