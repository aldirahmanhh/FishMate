const fishTypeModel = require('../models/fishTypeModel');

const getFish = async (req, res) => {
    try {
        const fish = await fishTypeModel.fishCollection();
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

const fishById = async (req, res) => {
    try {
        const { fishId } = req.params;
        const fish = await fishTypeModel.getFishById(fishId);

        if (!fish) {
            res.status(404).json({
                success: false,
                message: 'Ikan tidak ditemukan'
            })
        }

        res.status(200).json({
            success: true,
            data: fish
        })
    } catch (error) {
        console.error('Error in fishById:', error); // Log error untuk debugging
        res.status(500).json({
            error: true,
            message: 'Server Error',
            serverMessage: error.message || error
        });
    }
}

module.exports = {
    getFish,
    fishById
};
