import './Navbar.css'
import { NavLink, Link } from 'react-router-dom'
import Logo from './RemindMe.svg'
import { connect } from 'react-redux';
import { logout as handleLogout } from '../../actions/auth';

const Navbar = ({ user, isAuthenticated, logout }) => {
    const isLoggedIn = isAuthenticated;
    return (
        <div className="remindme-navbar-container">
            <nav className="remindme-navbar d-flex">
                <div className="remindme-nav-logo mr-auto p-2">
                    <NavLink to="/">
                        <img src={Logo} alt="RemindMe Icon"></img>
                    </NavLink>
                </div>
                { !isLoggedIn &&
                    <div className='button-wrap p-2'>
                        <button className="remindme-nav-button remindme-blue-border-button mr-1"><Link to="/login"><span>Login</span></Link></button>
                        <button className="remindme-nav-button remindme-blue-border-button ml-1"><Link to="/signup"><span>SignUp</span></Link></button>
                    </div>
                }
                { isLoggedIn &&
                    <div className='button-wrap p-2'>
                        <button className="remindme-nav-button remindme-blue-border-button mr-1" onClick={logout}><Link to="/"><span>Logout</span></Link></button>
                    </div>
                }
            </nav>
        </div>
    );
}

const mapStateToProps = (state) => ({
    isAuthenticated: state.auth.isAuthenticated,
    user: state.auth.user
})


export default connect(mapStateToProps, {logout: handleLogout})(Navbar);