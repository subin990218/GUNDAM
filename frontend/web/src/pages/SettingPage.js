import DetailSetting from '../components/Setting/DetailSetting';
// import Profile from '../components/Setting/Profile';
import styles from './SettingPage.module.css';

function Setting() {
    return ( 
        <div className={styles.container}>
            {/* <Profile /> */}
            <DetailSetting/>
        </div>
     );
}

export default Setting;