import React, { Component } from 'react'

import Section from 'components/Section';
import Details from 'components/Details';
import Navbar from 'components/Navbar';

import Northumberland from 'assets/Northumberland.jpeg';
import Seahouses from 'assets/Seahouses.jpg';
import HadriansWall from 'assets/HadriansWall.jpg';

import Menu from 'assets/Menu.png';
import MapExample from 'assets/Map.png';
import Learn from 'assets/Details.png';

export default class Home extends Component {
    render() {
        return (
            <div>
                <Section size="small" image={Northumberland} imageAlt="Northumberland">
                    <div className="container">
                        <div className="top">
                            <h1>Discover Northumberland</h1>
                            <h5>CSC2022 - Team 19</h5>
                        </div>
                    </div>
                </Section>
                <Details emoji={<i class="fas fa-shoe-prints"></i>} title="Explore" description="Find hundreds of interesting landmarks and locations in Northumberland Tyne and Wear">
                    <div className="col-sm">
                        <img className="box" src={Menu} />
                        <div className="text">
                            <p className="wrap">
                                Discover Northumberland has information on hundreds of different, unique locations across Northumberland Tyne and Wear,
                                including cultural, sport, cuisine and a number of other historic landmarks from all ages of history.
                            </p>
                            <p className="wrap">
                                Filter through all the locations there is to offer with our easy to use, intuitive menu system. Designed to create the greatest
                                user experience possible!
                            </p>
                        </div>
                    </div>
                    <div className="col-sm">
                        <img className="box" src={MapExample} />
                        <div className="text">
                            <p className="wrap">
                                Using Discover Northumberland's map and tour feature, you can create your own virtual tours. Choose where you want
                                to go and we will lead the way! 
                            </p>
                            <p className="wrap">
                                Use the map feature to pick places close to you, or search for specific locations you have in mind.
                                Creating tours is a simple and user friendly process, just add the places you want to visit and we will connect
                                the dots.
                            </p>
                        </div>
                    </div>
                </Details>
                <Section size="large" image={Seahouses} imageAlt="Seahouses"></Section>
                <Details emoji={<i class="fas fa-graduation-cap"></i>} title="Learn" description="Test">
                    <div className="col-6">
                        <img className="box" src={Learn} />
                        <div className="text">
                            <p className="wrap">
                                While using our virtual tours, just nearby or by clicking directly on the app you can learn about the history
                                of that landmark, with interesting facts and details, including a gallery of pictures you can save for yourself!
                            </p>
                        </div>
                    </div>
                    <div className="col-6">
                        <img className="box" src={Menu} />
                        <div className="text">
                            <p className="wrap">
                                Test
                            </p>
                        </div>
                    </div>
                </Details>
                <Section size="large" image={HadriansWall} imageAlt="Hardians Wall"></Section>
                <Details emoji={<i class="fas fa-graduation-cap"></i>} title="Learn" description="Test">
                    <div className="col-6">
                        <img className="box" src={Menu} />
                        <div className="text">
                            <p className="wrap">
                                Test
                            </p>
                        </div>
                    </div>
                    <div className="col-6">
                        <img className="box" src={Menu} />  
                        <div className="text">
                            <p className="wrap">
                                Test
                            </p>
                        </div>
                    </div>
                </Details>
            </div>
        )
    }
}
