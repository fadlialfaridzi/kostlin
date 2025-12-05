const pool = require('../config/database');

// Add to favorites
const addToFavorite = async (req, res) => {
    try {
        const userId = req.user.userId;
        const { kosId } = req.params;

        // Check if kos exists
        const [kosList] = await pool.execute(
            'SELECT id FROM kos_properties WHERE id = ?',
            [kosId]
        );

        if (kosList.length === 0) {
            return res.status(404).json({
                success: false,
                message: 'Kos not found'
            });
        }

        // Check if already favorited
        const [existingFavorites] = await pool.execute(
            'SELECT id, status FROM favorites WHERE user_id = ? AND kos_id = ?',
            [userId, kosId]
        );

        if (existingFavorites.length > 0) {
            const favorite = existingFavorites[0];
            if (favorite.status === 'ACTIVE') {
                return res.status(400).json({
                    success: false,
                    message: 'Kos already in favorites'
                });
            } else {
                // Reactivate favorite
                await pool.execute(
                    'UPDATE favorites SET status = "ACTIVE", updated_at = NOW() WHERE id = ?',
                    [favorite.id]
                );
            }
        } else {
            // Add new favorite
            await pool.execute(
                'INSERT INTO favorites (user_id, kos_id, status) VALUES (?, ?, "ACTIVE")',
                [userId, kosId]
            );
        }

        res.json({
            success: true,
            message: 'Kos added to favorites'
        });
    } catch (error) {
        console.error('Add to favorite error:', error);
        res.status(500).json({
            success: false,
            message: 'Internal server error',
            error: error.message
        });
    }
};

// Remove from favorites
const removeFromFavorite = async (req, res) => {
    try {
        const userId = req.user.userId;
        const { kosId } = req.params;

        const [result] = await pool.execute(
            'UPDATE favorites SET status = "REMOVED", updated_at = NOW() WHERE user_id = ? AND kos_id = ?',
            [userId, kosId]
        );

        if (result.affectedRows === 0) {
            return res.status(404).json({
                success: false,
                message: 'Favorite not found'
            });
        }

        res.json({
            success: true,
            message: 'Kos removed from favorites'
        });
    } catch (error) {
        console.error('Remove from favorite error:', error);
        res.status(500).json({
            success: false,
            message: 'Internal server error',
            error: error.message
        });
    }
};

// Get user favorites
const getUserFavorites = async (req, res) => {
    try {
        const userId = req.user.userId;

        const [favorites] = await pool.execute(
            `SELECT f.id, f.created_at as date_added, f.status,
                    k.id as kos_id, k.name, k.location, k.price_per_month, k.rating_value, 
                    k.type, k.description, k.image_url, k.is_popular, k.is_recommended
             FROM favorites f
             JOIN kos_properties k ON f.kos_id = k.id
             WHERE f.user_id = ? AND f.status = 'ACTIVE'
             ORDER BY f.created_at DESC`,
            [userId]
        );

        const favoritesWithFacilities = await Promise.all(
            favorites.map(async (favorite) => {
                const [facilities] = await pool.execute(
                    'SELECT name FROM kos_facilities WHERE kos_id = ?',
                    [favorite.kos_id]
                );

                return {
                    id: favorite.id,
                    kosProperty: {
                        id: favorite.kos_id,
                        name: favorite.name,
                        location: favorite.location,
                        price: `Rp ${favorite.price_per_month.toLocaleString('id-ID')}/Bulan`,
                        pricePerMonth: favorite.price_per_month,
                        rating: favorite.rating_value.toFixed(1),
                        ratingValue: parseFloat(favorite.rating_value),
                        type: favorite.type,
                        facilities: facilities.map(f => f.name),
                        description: favorite.description,
                        imageUrl: favorite.image_url,
                        isPopular: favorite.is_popular === 1,
                        isRecommended: favorite.is_recommended === 1
                    },
                    dateAdded: formatDate(favorite.date_added),
                    status: favorite.status
                };
            })
        );

        res.json({
            success: true,
            data: favoritesWithFacilities,
            count: favoritesWithFacilities.length
        });
    } catch (error) {
        console.error('Get user favorites error:', error);
        res.status(500).json({
            success: false,
            message: 'Internal server error',
            error: error.message
        });
    }
};

// Check if kos is favorite
const isFavorite = async (req, res) => {
    try {
        const userId = req.user.userId;
        const { kosId } = req.params;

        const [favorites] = await pool.execute(
            'SELECT id FROM favorites WHERE user_id = ? AND kos_id = ? AND status = "ACTIVE"',
            [userId, kosId]
        );

        res.json({
            success: true,
            data: {
                isFavorite: favorites.length > 0
            }
        });
    } catch (error) {
        console.error('Check favorite error:', error);
        res.status(500).json({
            success: false,
            message: 'Internal server error',
            error: error.message
        });
    }
};

// Helper function to format date
const formatDate = (date) => {
    const now = new Date();
    const favoriteDate = new Date(date);
    const diffTime = Math.abs(now - favoriteDate);
    const diffDays = Math.floor(diffTime / (1000 * 60 * 60 * 24));

    if (diffDays === 0) return 'Hari ini';
    if (diffDays === 1) return 'Kemarin';
    if (diffDays < 7) return `${diffDays} hari lalu`;
    if (diffDays < 30) return `${Math.floor(diffDays / 7)} minggu lalu`;
    return `${Math.floor(diffDays / 30)} bulan lalu`;
};

module.exports = {
    addToFavorite,
    removeFromFavorite,
    getUserFavorites,
    isFavorite
};

