import React, { useState } from 'react';
import './NotificationBell.css';
import Notification from './Notification.svg';
import Stomp from 'stompjs'
import { connect } from 'react-redux';
import { load_user as handleLoadUser } from '../../actions/auth';
import axios from 'axios';
import { Row, Col } from 'react-bootstrap';

const NotificationBell = React.memo(({ user, isAuthenticated, load_user }) => {

    const [showNotificationBox, setShowNotificationBox] = useState(false);
    const [itemList, setItemList] = useState([]);
    var [newNotification, setNewNotification] = useState(0);

    const messageCallBack = function (message) {
        if (message.body) {
            setNewNotification(++newNotification)
            var b = JSON.parse(message.body)
            b.notification_time = new Date(b.notification_time)
            itemList.push(b)
            setItemList(itemList);
        }
    }

    var calledTimes = 0
    const createWebSocket = (queue) => {
        if (calledTimes > 0)
            return
        calledTimes++

        var url = "wss://cloud.dirtboll.com:15673/ws";
        var wss = new WebSocket(url);
        var client = Stomp.over(wss);
        var connect_callback = function () {
            console.log('connected websocket!');
            client.subscribe(`/queue/${queue}`, messageCallBack, { durable: false, exclusive: false, "auto-delete": true });
        };
        var error_callback = function (error) {
            console.log('unable to connect to websocket.');
            console.log(error);
        };
        client.debug = null;
        client.connect('receiver', 'receiver321', connect_callback, error_callback, '/');
    }

    React.useEffect(() => {
        if (user && user[0])
            createWebSocket(user[0].username)
    }, [user])

    const config = {
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `JWT ${localStorage.getItem('access')}`,
            'Accept': 'application/json'
        }
    };

    const MIN = 60
    const HOUR = 60 * MIN
    const DAY = 24 * HOUR
    function diffTime(date) {
        var diff = Math.round((new Date() - date) / 1000)

        var days = Math.round(diff / DAY)
        if (days > 0)
            return `${days} day${days > 1 ? "s" : ""} ago`

        diff %= DAY
        var hours = Math.round(diff / HOUR)
        if (hours > 0)
            return `${hours} hour${hours > 1 ? "s" : ""} ago`

        diff %= HOUR
        var minutes = Math.round(diff / MIN)
        if (minutes > 0)
            return `${minutes} minute${minutes > 1 ? "s" : ""} ago`

        diff %= MIN
        return `${diff} second${diff > 1 ? "s" : ""} ago`
    }


    function handleClickNotif() {
        setNewNotification(0)
        if (showNotificationBox) {
            itemList.forEach(v => v.is_read = true)
        }
        setShowNotificationBox(!showNotificationBox);
    }

    return (
        <div className='remindme-notification-bell'>
            <button onClick={handleClickNotif}>
                <img src={Notification} alt="Notification Bell" />
                <div className="d-flex flex-row align-items-center justify-content-center" id="notification-count"><p>{newNotification}</p></div>
            </button>
            <div id="remindme-notification-box" className="px-3 py-3" style={{ display: showNotificationBox ? 'block' : 'none', overflow: "hidden" }}>
                <div className='px-1 scrollbar scrollbar-primary' style={{ maxWidth: "30em", maxHeight: "20em", overflowY: "auto", overflowX: "hidden" }}>
                    {itemList.length !== 0 ? itemList.slice().reverse().map((item, i) => (
                        <Row key={i} className={`${i !== itemList.length - 1 ? 'mb-3' : ''}`} as="div">
                            <Col xs="7" style={{ fontSize: "14px", color: "#546FB2", fontWeight: item.is_read ? "400" : "900" }}>{item.name}</Col>
                            <Col style={{ fontSize: "12px", textAlign: "right" }}>{diffTime(item.notification_time)}</Col>
                        </Row>
                    )) : <p className='m-0'>There is no item to be reminded!</p>}
                </div>
            </div>
        </div>
    )
});

const mapStateToProps = (state) => ({
    isAuthenticated: state.auth.isAuthenticated,
    user: state.auth.user
})

export default connect(mapStateToProps, { load_user: handleLoadUser })(NotificationBell);