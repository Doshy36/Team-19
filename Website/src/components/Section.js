import React, { Component } from 'react'

import classNames from 'classnames';

import PropTypes from 'prop-types';

export default class Section extends Component {

    constructor(props) {
        super(props);
    }

    render() {
        return (
            <div className={classNames("section", this.props.size)}>
                <img src={this.props.image} alt={this.props.imageAlt} />
                {this.props.children}
            </div>
        )
    }
}

Section.propTypes = {
    image: PropTypes.arrayOf(PropTypes.string, PropTypes.object).isRequired,
    imageAlt: PropTypes.string.isRequired,
    size: PropTypes.string
}

Section.defaultProps = {
    size: "small"
}