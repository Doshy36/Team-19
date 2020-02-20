import React, { Component } from 'react';
import { BrowserRouter as Router, Switch, Route } from 'react-router-dom';

import Home from 'views/Home';

import 'styles/main.css';

export default class App extends Component {

	constructor(props) {
		super(props);
	}

	render() {
		return (
			<Router>
				<Switch>
					<Route exact path="/" component={Home} />
				</Switch>
			</Router>
		);
	}
}