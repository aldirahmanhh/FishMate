const express = require('express');
const FishType = require('../controller/FishType');

const fishRouter = express.Router();

fishRouter.get('/getFish', FishType.getFish);

module.exports = fishRouter;