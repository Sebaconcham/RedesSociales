'use strict';
var Sequelize = require('sequelize');
module.exports = function (sequelize, DataTypes) {
    var usuario = sequelize.define('usuario', {
        id_android: {
            type: DataTypes.STRING
        },
        nombre: {
            type: DataTypes.STRING
        }
    });
    return usuario;
};