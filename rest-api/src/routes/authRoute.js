const express = require('express');
const Auth = require('../controller/Auth');

const router = express.Router();

router.post('/register', Auth.register);
router.post('/login', Auth.login);
router.post('/changePassword', Auth.changePassword);
router.post('/changeUsername', Auth.changeUsername);

module.exports = router;