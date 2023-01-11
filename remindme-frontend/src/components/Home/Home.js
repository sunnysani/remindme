import './Home.css'
import Icon from './Icon.svg'
import { connect } from 'react-redux';
import Main from '../Main/Main'

const Home = ({ user, isAuthenticated }) => {
    if (!isAuthenticated) {
        return (
            <div className="remindme-home-sect-2-bg">
                <div className="remindme-home-sect-2-container">
                    <div className="remindme-home-sect-2-text">
                        <h1 className="remindme-home-headings">RemindMe!</h1>
                        <p className="remindme-home-p">RemindMe! is a reminder web application that can remind and schedule important tasks.</p>
                    </div>
                    <div className="remindme-home-sect-2-icon">
                        <img src={Icon} alt="Walkiddie Icon"></img>
                    </div>
                </div>
            </div>
        );
    } else {
        return (
            <Main />
        );
    }
}

const mapStateToProps = (state) => ({
    isAuthenticated: state.auth.isAuthenticated,
    user: state.auth.user
})

export default connect(mapStateToProps)(Home);