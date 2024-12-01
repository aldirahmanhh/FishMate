const express = require('express');
const Auth = require('../controller/Auth');

const router = express.Router();

router.post('/register', Auth.register);
router.post('/login', Auth.login);

module.exports = router;