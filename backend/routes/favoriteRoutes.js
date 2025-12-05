const express = require('express');
const router = express.Router();
const favoriteController = require('../controllers/favoriteController');
const { authenticateToken } = require('../middleware/auth');

// All routes require authentication
router.use(authenticateToken);

router.post('/:kosId', favoriteController.addToFavorite);
router.delete('/:kosId', favoriteController.removeFromFavorite);
router.get('/', favoriteController.getUserFavorites);
router.get('/check/:kosId', favoriteController.isFavorite);

module.exports = router;

