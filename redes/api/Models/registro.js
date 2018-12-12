'use strict';
var Sequelize = require('sequelize');
module.exports = function (sequelize, DataTypes) {
    var registro = sequelize.define('registro', {
        beacon: {
            type: DataTypes.STRING
        },
        estado: {
            type: DataTypes.ENUM('Entrada', 'Salida')
        },
        hora: {
        	type: DataTypes.TIME()
        }
    });
    return registro;
};