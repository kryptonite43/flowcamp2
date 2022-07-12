const { text } = require('express');
const express = require('express');
const mongoose = require('mongoose');
const server = express();
const SearchRecord = require('./models/SearchRecord');
require('dotenv').config({ path: 'variables.env' });

server.use(express.json());

server.post('/', (req, res) => {
    const newSearchRecord = new SearchRecord();
    newSearchRecord.email = req.body.email;
    newSearchRecord.text = req.body.text;
    newSearchRecord.get = true;
    newSearchRecord.save().then((record) => {
        res.status(200).send();
        console.log(record);
    }).catch((err) => {
        res.status(400).send();
        console.log(err);
    });
})

server.get('/', (req, res) => {
    SearchRecord.aggregate([{
        $match: {
            email: req.query.email,
            get: true
        }
    }]).group({
        _id: "$text",
        time: {
            $max: "$createdAt"
        }
    }).sort({
        time: -1
    }).limit(10).then((result) => {
        var texts = result.map(r => r._id)
        res.status(200).send(texts);
        console.log(result);
    }).catch((err) => {
        res.status(400).send();
        console.log(err);
    })
})

server.get('/s', (req, res) => {
    SearchRecord.aggregate([{
        $match: {
            email: req.query.email,
            text: {
                $regex: "^"+req.query.subtext
            },
            get: true
        }
    }]).group({
        _id: "$text",
        time: {
            $max: "$createdAt"
        }
    }).sort({
        time: -1
    }).limit(10).then((result) => {
        var texts = result.map(r => r._id)
        res.status(200).send(texts);
        console.log(result);
    }).catch((err) => {
        res.status(400).send();
        console.log(err);
    })
})

server.get('/r', (req, res) => {
    SearchRecord.aggregate([{
        $match: {
            updatedAt: {
                $gt: new Date(req.query.timestamp-36000000)
            }
        }
    }]).sortByCount("text").limit(10).then((result) => {
        var texts = result.map(r => r._id)
        res.status(200).send(texts);
        console.log(result);
    }).catch((err) => {
        res.status(400).send();
        console.log(err);
    })
})

server.put('/', (req, res) => {
    SearchRecord.updateMany({
        email: req.body.email,
        text: req.body.text
    }, {
        get: false
    }).then((record) => {
        res.status(200).send();
        console.log(record);
    }).catch((err) => {
        res.status(400).send();
        console.log(err);
    })
})

server.listen(443, (err) => {
    if (err) {
        console.log(err);
    } else {
        mongoose.connect(process.env.MONGODB_URL, {useNewUrlParser: true}, (err) => {
            if (err) {
                console.log(err);
            } else {
                console.log('Connected');
            }
        })
    }
})