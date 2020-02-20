import React, { Component } from 'react'

import { Link } from 'react-router-dom';

export default class Navbar extends Component {
    render() {
        return (
            <div className="navbar-holder">
                <ul className="navbar">
                    <li><Link href="/" active={this.props.active === "home"}>Home</Link></li>
                </ul>
            </div>
        )
    }
}
