const express = require('express');
const FishType = require('../controller/FishType');
const authToken = require('../middleware/authMiddleware');


const fishRouter = express.Router();

fishRouter.get('/getFish', authToken, FishType.getFish);
fishRouter.get('/getFish/:fishId', authToken, FishType.fishById);

module.exports = fishRouter;