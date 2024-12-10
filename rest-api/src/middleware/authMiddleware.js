const jwt = require('jsonwebtoken');

const authToken = (req, res, next) => {
    try {
        const authHeader = req.headers.authorization;

        if (!authHeader) {
            return res.status(400).json({
                error: true,
                message: 'Akses ditolak. Tidak ada Token'
            });
        };

        const token = authHeader.split(' ')[1]
        if (!token) {
            return res.status(400).json({
                error: true,
                message: 'Akses ditolak. Tidak ada Token'
            });
        };

        const decoded = jwt.verify(token, 'secretkey');
        req.user = decoded;
        next();
    } catch (error) {
        console.error('Error in token:', error); // Log error untuk debugging
        res.status(401).json({
            error: true,
            message: 'Token tidak valid',
            serverMessage: error.message || error
        });
    };
};

module.exports = authToken;