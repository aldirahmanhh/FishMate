const express = require('express');
const FishType = require('../controller/FishType');

const fishRouter = express.Router();

fishRouter.get('/getFish', FishType.getFish);
fishRouter.get('/getFish/:fishId',FishType.fishById);

module.exports = fishRouter;