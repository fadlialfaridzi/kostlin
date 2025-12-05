require('dotenv').config();
const express = require('express');
const cors = require('cors');

// Initialize database pool and test connection on startup
require('./config/database');

const authRoutes = require('./routes/authRoutes');
const kosRoutes = require('./routes/kosRoutes');
const favoriteRoutes = require('./routes/favoriteRoutes');

const app = express();

// CORS configuration
app.use(cors({
    origin: '*', // Allow all origins for development
    credentials: true
}));

app.use(express.json());
app.use(express.urlencoded({ extended: true }));

// Health check
app.get('/', (_req, res) => {
    res.json({ 
        status: 'ok', 
        message: 'Kostlin API is running',
        version: '1.0.0'
    });
});

// API routes
app.use('/api/auth', authRoutes);
app.use('/api/kos', kosRoutes);
app.use('/api/favorites', favoriteRoutes);

// 404 handler
app.use((req, res) => {
    res.status(404).json({ 
        success: false, 
        message: 'Route not found' 
    });
});

// Centralized error handler
// eslint-disable-next-line no-unused-vars
app.use((err, _req, res, _next) => {
    console.error('Unhandled error:', err);
    res.status(err.status || 500).json({
        success: false,
        message: err.message || 'Internal server error'
    });
});

const PORT = process.env.PORT || 5000;
app.listen(PORT, () => {
    console.log(`🚀 Kostlin API server listening on port ${PORT}`);
    console.log(`📡 API Base URL: http://localhost:${PORT}`);
    console.log(`📚 API Documentation: http://localhost:${PORT}/api`);
});

