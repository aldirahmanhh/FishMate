require('dotenv').config();
const authRoute = require('./routes/authRoute');
const fishTypeRoute = require('./routes/fishTypeRoute.js');
const middlewareLogRequest = require('./middleware/logs.js');


const PORT = process.env.PORT || 5000;
const express = require('express');

const app = express();
app.use(middlewareLogRequest);
app.use(express.json()); 

app.use('/auth', authRoute);
app.use('/fish', fishTypeRoute)

app.use((err, req, res, next) => {
    res.json({
        message: err.message
    })
})

app.listen(PORT, () => {
    console.log(`Server running on port ${PORT}`)
})