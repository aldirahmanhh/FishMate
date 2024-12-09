const fishTypeModel = require('../models/fishTypeModel');

const getFish = async (req, res) => {
    try {
        const fish = await fishTypeModel();
        res.status(200).json({
            success: true,
            data: fish,
        })
    } catch (error) {
        console.error('Error in getfish:', error); // Log error untuk debugging
        res.status(500).json({
            error: true,
            message: 'Server Error',
            serverMessage: error.message || error
        });
    }
}

module.exports = {getFish};
