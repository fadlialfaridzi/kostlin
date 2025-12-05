const express = require('express');
const router = express.Router();
const kosController = require('../controllers/kosController');

router.get('/', kosController.getAllKos);
router.get('/popular', kosController.getPopularKos);
router.get('/recommended', kosController.getRecommendedKos);
router.get('/search', kosController.searchKos);
router.get('/:id', kosController.getKosById);

module.exports = router;

