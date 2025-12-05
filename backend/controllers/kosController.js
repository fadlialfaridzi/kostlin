const pool = require('../config/database');

// Helper to safely normalize rating values
const normalizeRating = (value) => {
    const num = Number.parseFloat(value);
    const safe = Number.isFinite(num) ? num : 0;
    return {
        rating: safe.toFixed(1),
        ratingValue: safe
    };
};

// Get all kos properties with optional filters
const getAllKos = async (req, res) => {
    try {
        const { type, minPrice, maxPrice, search, popular, recommended } = req.query;
        
        let query = 'SELECT * FROM kos_properties WHERE 1=1';
        const params = [];

        if (type && ['PUTRA', 'PUTRI', 'CAMPUR'].includes(type.toUpperCase())) {
            query += ' AND type = ?';
            params.push(type.toUpperCase());
        }

        if (minPrice) {
            query += ' AND price_per_month >= ?';
            params.push(parseInt(minPrice));
        }

        if (maxPrice) {
            query += ' AND price_per_month <= ?';
            params.push(parseInt(maxPrice));
        }

        if (search) {
            query += ' AND (name LIKE ? OR location LIKE ? OR description LIKE ?)';
            const searchTerm = `%${search}%`;
            params.push(searchTerm, searchTerm, searchTerm);
        }

        if (popular === 'true') {
            query += ' AND is_popular = TRUE';
        }

        if (recommended === 'true') {
            query += ' AND is_recommended = TRUE';
        }

        query += ' ORDER BY created_at DESC';

        const [kosList] = await pool.execute(query, params);

        // Get facilities for each kos
        const kosWithFacilities = await Promise.all(
            kosList.map(async (kos) => {
                const [facilities] = await pool.execute(
                    'SELECT name, icon, is_available FROM kos_facilities WHERE kos_id = ?',
                    [kos.id]
                );

                // Calculate average rating from reviews
                const [ratingResult] = await pool.execute(
                    'SELECT AVG(rating) as avg_rating, COUNT(*) as review_count FROM reviews WHERE kos_id = ?',
                    [kos.id]
                );

                const avgRating = ratingResult[0].avg_rating ?? kos.rating_value ?? 0;
                const { rating, ratingValue } = normalizeRating(avgRating);
                const reviewCount = ratingResult[0].review_count || 0;

                return {
                    id: kos.id,
                    name: kos.name,
                    location: kos.location,
                    price: `Rp ${kos.price_per_month.toLocaleString('id-ID')}/Bulan`,
                    pricePerMonth: kos.price_per_month,
                    rating,
                    ratingValue,
                    type: kos.type,
                    facilities: facilities.map(f => f.name),
                    description: kos.description,
                    imageUrl: kos.image_url,
                    isPopular: kos.is_popular === 1,
                    isRecommended: kos.is_recommended === 1,
                    ownerEmail: kos.owner_email,
                    ownerPhone: kos.owner_phone,
                    latitude: kos.latitude,
                    longitude: kos.longitude,
                    reviewCount
                };
            })
        );

        res.json({
            success: true,
            data: kosWithFacilities,
            count: kosWithFacilities.length
        });
    } catch (error) {
        console.error('Get all kos error:', error);
        res.status(500).json({
            success: false,
            message: 'Internal server error',
            error: error.message
        });
    }
};

// Get kos by ID
const getKosById = async (req, res) => {
    try {
        const { id } = req.params;

        const [kosList] = await pool.execute(
            'SELECT * FROM kos_properties WHERE id = ?',
            [id]
        );

        if (kosList.length === 0) {
            return res.status(404).json({
                success: false,
                message: 'Kos not found'
            });
        }

        const kos = kosList[0];

        // Get facilities
        const [facilities] = await pool.execute(
            'SELECT name, icon, is_available FROM kos_facilities WHERE kos_id = ?',
            [id]
        );

        // Get reviews
        const [reviews] = await pool.execute(
            `SELECT r.id, r.rating, r.comment, r.is_verified, r.created_at,
                    u.full_name as user_name, u.avatar_url as user_avatar
             FROM reviews r
             JOIN users u ON r.user_id = u.id
             WHERE r.kos_id = ?
             ORDER BY r.created_at DESC`,
            [id]
        );

        // Calculate average rating
        const [ratingResult] = await pool.execute(
            'SELECT AVG(rating) as avg_rating, COUNT(*) as review_count FROM reviews WHERE kos_id = ?',
            [id]
        );

        const avgRating = ratingResult[0].avg_rating ?? kos.rating_value ?? 0;
        const { rating, ratingValue } = normalizeRating(avgRating);
        const reviewCount = ratingResult[0].review_count || 0;

        res.json({
            success: true,
            data: {
                id: kos.id,
                name: kos.name,
                location: kos.location,
                price: `Rp ${kos.price_per_month.toLocaleString('id-ID')}/Bulan`,
                pricePerMonth: kos.price_per_month,
                rating,
                ratingValue,
                type: kos.type,
                facilities: facilities.map(f => ({
                    name: f.name,
                    icon: f.icon,
                    isAvailable: f.is_available === 1
                })),
                description: kos.description,
                imageUrl: kos.image_url,
                isPopular: kos.is_popular === 1,
                isRecommended: kos.is_recommended === 1,
                ownerEmail: kos.owner_email,
                ownerPhone: kos.owner_phone,
                latitude: kos.latitude,
                longitude: kos.longitude,
                reviewCount,
                reviews: reviews.map(r => ({
                    id: r.id,
                    userName: r.user_name,
                    userAvatar: r.user_avatar,
                    rating: parseFloat(r.rating),
                    comment: r.comment,
                    date: formatDate(r.created_at),
                    isVerified: r.is_verified === 1
                }))
            }
        });
    } catch (error) {
        console.error('Get kos by ID error:', error);
        res.status(500).json({
            success: false,
            message: 'Internal server error',
            error: error.message
        });
    }
};

// Get popular kos
const getPopularKos = async (req, res) => {
    try {
        const [kosList] = await pool.execute(
            'SELECT * FROM kos_properties WHERE is_popular = TRUE ORDER BY rating_value DESC LIMIT 10'
        );

        const kosWithFacilities = await Promise.all(
            kosList.map(async (kos) => {
                const [facilities] = await pool.execute(
                    'SELECT name FROM kos_facilities WHERE kos_id = ?',
                    [kos.id]
                );

                const { rating, ratingValue } = normalizeRating(kos.rating_value);

                return {
                    id: kos.id,
                    name: kos.name,
                    location: kos.location,
                    price: `Rp ${kos.price_per_month.toLocaleString('id-ID')}/Bulan`,
                    pricePerMonth: kos.price_per_month,
                    rating,
                    ratingValue,
                    type: kos.type,
                    facilities: facilities.map(f => f.name),
                    description: kos.description,
                    imageUrl: kos.image_url,
                    isPopular: true,
                    isRecommended: kos.is_recommended === 1
                };
            })
        );

        res.json({
            success: true,
            data: kosWithFacilities
        });
    } catch (error) {
        console.error('Get popular kos error:', error);
        res.status(500).json({
            success: false,
            message: 'Internal server error',
            error: error.message
        });
    }
};

// Get recommended kos
const getRecommendedKos = async (req, res) => {
    try {
        const [kosList] = await pool.execute(
            'SELECT * FROM kos_properties WHERE is_recommended = TRUE ORDER BY rating_value DESC LIMIT 10'
        );

        const kosWithFacilities = await Promise.all(
            kosList.map(async (kos) => {
                const [facilities] = await pool.execute(
                    'SELECT name FROM kos_facilities WHERE kos_id = ?',
                    [kos.id]
                );

                const { rating, ratingValue } = normalizeRating(kos.rating_value);

                return {
                    id: kos.id,
                    name: kos.name,
                    location: kos.location,
                    price: `Rp ${kos.price_per_month.toLocaleString('id-ID')}/Bulan`,
                    pricePerMonth: kos.price_per_month,
                    rating,
                    ratingValue,
                    type: kos.type,
                    facilities: facilities.map(f => f.name),
                    description: kos.description,
                    imageUrl: kos.image_url,
                    isPopular: kos.is_popular === 1,
                    isRecommended: true
                };
            })
        );

        res.json({
            success: true,
            data: kosWithFacilities
        });
    } catch (error) {
        console.error('Get recommended kos error:', error);
        res.status(500).json({
            success: false,
            message: 'Internal server error',
            error: error.message
        });
    }
};

// Search kos
const searchKos = async (req, res) => {
    try {
        const { q } = req.query;

        if (!q || q.trim() === '') {
            return res.json({
                success: true,
                data: [],
                count: 0
            });
        }

        const searchTerm = `%${q.trim()}%`;
        const [kosList] = await pool.execute(
            `SELECT DISTINCT k.* FROM kos_properties k
             LEFT JOIN kos_facilities f ON k.id = f.kos_id
             WHERE k.name LIKE ? OR k.location LIKE ? OR k.description LIKE ? OR f.name LIKE ?
             ORDER BY k.rating_value DESC`,
            [searchTerm, searchTerm, searchTerm, searchTerm]
        );

        const kosWithFacilities = await Promise.all(
            kosList.map(async (kos) => {
                const [facilities] = await pool.execute(
                    'SELECT name FROM kos_facilities WHERE kos_id = ?',
                    [kos.id]
                );

                const { rating, ratingValue } = normalizeRating(kos.rating_value);

                return {
                    id: kos.id,
                    name: kos.name,
                    location: kos.location,
                    price: `Rp ${kos.price_per_month.toLocaleString('id-ID')}/Bulan`,
                    pricePerMonth: kos.price_per_month,
                    rating,
                    ratingValue,
                    type: kos.type,
                    facilities: facilities.map(f => f.name),
                    description: kos.description,
                    imageUrl: kos.image_url,
                    isPopular: kos.is_popular === 1,
                    isRecommended: kos.is_recommended === 1
                };
            })
        );

        res.json({
            success: true,
            data: kosWithFacilities,
            count: kosWithFacilities.length
        });
    } catch (error) {
        console.error('Search kos error:', error);
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
    const reviewDate = new Date(date);
    const diffTime = Math.abs(now - reviewDate);
    const diffDays = Math.floor(diffTime / (1000 * 60 * 60 * 24));

    if (diffDays === 0) return 'Hari ini';
    if (diffDays === 1) return '1 hari lalu';
    if (diffDays < 7) return `${diffDays} hari lalu`;
    if (diffDays < 30) return `${Math.floor(diffDays / 7)} minggu lalu`;
    if (diffDays < 365) return `${Math.floor(diffDays / 30)} bulan lalu`;
    return `${Math.floor(diffDays / 365)} tahun lalu`;
};

module.exports = {
    getAllKos,
    getKosById,
    getPopularKos,
    getRecommendedKos,
    searchKos
};

