const mongoose = require('mongoose');
const { Schema } = mongoose;

const searchRecordSchema = new Schema({
    email:{
        type: String,
        required: true
    },
    text: String,
    get: Boolean
}, {
    timestamps: true
});

module.exports = mongoose.model('SearchRecord', searchRecordSchema);