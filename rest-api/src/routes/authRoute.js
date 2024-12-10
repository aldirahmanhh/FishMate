const express = require('express');
const Auth = require('../controller/Auth');
const authToken = require('../middleware/authMiddleware');

const authRouter = express.Router();

authRouter.post('/register', Auth.register);
authRouter.post('/login', Auth.login);
authRouter.post('/changePassword', authToken, Auth.changePassword);
authRouter.post('/changeUsername', authToken, Auth.changeUsername);


module.exports = authRouter;