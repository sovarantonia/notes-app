import React from 'react';
import {Link} from 'react-router-dom';
import Header from "./header";
import '../resources/landing-page.css';

const LandingPage = () => {
    return (
        <div className="landingPageContainer">
            <Header/>
            <div className="contentContainer">
                <p>
                    The perfect place to keep track of your thoughts, ideas, and progress.
                </p>
                <p>
                    Whether you're working on self-development or motivation, we make it easier to create, organize, and
                    share your notes.
                </p>
                <p>
                    Track your progress over time and access your notes anytime, anywhere.
                    Stay motivated and focused on your journey.
                </p>

                <h1>Empower Your Personal Growth</h1>
                <p>Create notes that inspire your growth. Document your journey, track milestones, and stay motivated to
                    improve every day.</p>

                <h1>Keep Your Motivation on Track</h1>
                <p>Visualize your progress and stay focused on your goals with our progress tracking features.</p>

                <h1>Your Notes, Anytime, Anywhere</h1>
                <p>Access your personal notes across devices and stay on track with your self-development journey.</p>

                <h1>Join a Motivated Community</h1>
                <p>Connect with others who are on a journey to improve themselves, just like you.</p>

                <div className="authLinks">
                    <Link to="/about" id="aboutButton">Learn more about us</Link>
                    <Link to="/login" id="loginButton">Start your journey now</Link>
                </div>
            </div>


        </div>
    );
}

export default LandingPage;