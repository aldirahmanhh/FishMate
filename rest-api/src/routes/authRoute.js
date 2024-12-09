const express = require('express');
const Auth = require('../controller/Auth');


const authRouter = express.Router();

authRouter.post('/register', Auth.register);
authRouter.post('/login', Auth.login);
authRouter.post('/changePassword', Auth.changePassword);
authRouter.post('/changeUsername', Auth.changeUsername);


module.exports = authRouter;