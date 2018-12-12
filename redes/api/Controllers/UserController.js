var express = require('express');
var router = express.Router();
var model = require('../Models/index');

router.get('/', function (req, res, next) {
	model.usuario.findAll().then(users => 
		{
			if (users.length == 0) {
				res.status(404).json(users)
			}
			else{
				res.status(200).json(users)
			}
		}
	).catch(error => res.status(400).json({
		error: true,
		data: [],
		error: error
   }));
});

router.get('/id/:id', function (req, res, next) {
	model.usuario.findOne({
		where: {
			id: req.params.id
		},
	})
		.then(
			users => 
		{
			if (users != null) {
				res.status(200).json(users)    
			}
			else{
				res.status(404).json(users)
			}
		}
		)   
		.catch(error => res.status(400).json({
			error: true,
			data: [],
			error: error
		}));
});

router.get('/id_android/:id', function (req, res, next) {
	model.usuario.findOne({
		where: {
			id_android: req.params.id
		},
	})
		.then(users => 
		{
			if (users != null) {
				res.status(200).json(users)    
			}
			else{
				res.status(404).json(users)
			}
		}
		)   
		.catch(error => res.status(400).json({
			error: true,
			data: [],
			error: error
		}));
});

router.post('/', function (req, res, next) {
	console.log(req.body.id_android);
	console.log(req.body.nombre);
	if(req.body.id_android == null || req.body.nombre == null){
		res.status(400).json({
			error: true,
			data: [],
			error: "Faltan datos"
		});
	}else{
		model.usuario.create({
			id_android: req.body.id_android,
			nombre: req.body.nombre
		})
		.then(usuario => res.status(201).json(usuario))
		.catch(error => res.status(400).json({
			error: true,
			data: [],
			error: error
		}));
	}
});

router.put('/:id', function (req, res, next) {
	if(req.body.nombre == null){
		res.status(400).json({
			error: true,
			data: [],
			error: "Faltan datos"
		});
	}else{
		model.usuario.update({
			nombre: req.body.nombre
		}, {
			where: {
				id: req.params.id
			}
		}).then(usuario => {
			if (usuario[0] == 0) {
				console.log("404");
				res.status(404).json({
					error: true,
					data: [],
					error: "no encontrado"
				})
			}
			else{
				res.status(200).json({
					"id": null,
					"id_android": null,
					"nombre": req.body.nombre,
					"createdAt": null,
					"updatedAt": null
			})
		}}).catch(error => res.status(400).json({
			error: true,
			error: error
		}));
	}
});

router.delete('/:id', function (req, res, next) {
	model.usuario.destroy({ where: {
		id: req.params.id
	}})
		.then(status => res.status(201).json({
				"id": null,
				"id_android": "delete",
				"nombre": "delete",
				"createdAt": null,
				"updatedAt": null
			}))
		.catch(error => res.status(400).json({
			error: true,
			error: error
		}));
});

module.exports = router;