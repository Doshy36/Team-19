import React, { Component } from 'react'

export default class Details extends Component {

    constructor(props) {
        super(props);
    }

    render() {
        return (
            <>
                <h3 className="content">{this.props.emoji} {this.props.title}</h3>
                <h5 className="content">{this.props.description}</h5>
                <hr className="line" />
                <div className="details">
                    {this.props.children}
                </div>
            </>
        )
    }
}
