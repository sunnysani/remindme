import './Main.css';
import { connect } from 'react-redux';
import NotificationBell from '../NotificationBell/NotificationBell';
import Reminder from '../Reminder/Reminder';
import Scheduler from '../Scheduler/Scheduler';


const Main = ({ user, isAuthenticatd }) => {
    console.log(user)
    return (
        <div className='remindme-main-content'>
            <div className='remindme-main-header'>
                <div className='remindme-main-header-left'>
                    <h1>{user && user[0] ? `Hi, ${user[0].firstName} ${user[0].lastName}` : "Hello"}!</h1>
                    <p>Server Time, {new Date().toLocaleString()}</p>
                </div>
                <div className='remindme-main-header-right'>
                    <NotificationBell />
                </div>
            </div>
            <div className='remindme-main-body'>
                <Scheduler />
                <Reminder />
            </div>
        </div>
    );
}

const mapStateToProps = (state) => ({
    isAuthenticated: state.auth.isAuthenticated,
    user: state.auth.user
})

export default connect(mapStateToProps)(Main);