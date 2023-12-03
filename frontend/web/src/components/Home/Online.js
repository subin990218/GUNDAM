import styles from "./Online.module.css";
import OnlineUser from "../Common/OnlineUser";

function Online({ onlineUser }) {
  // const onlineuserlist = [
  //   {
  //     name: "subin",
  //     img: "https://search.pstatic.net/common/?src=http%3A%2F%2Fblogfiles.naver.net%2FMjAyMzA4MTZfMTE5%2FMDAxNjkyMTY1ODEzNTgy.iJ0OgFhqLX7zgG2pRBgqrGrDKflLFVQafiuqVJYtoYcg.MueNDVOdzUEZjippuH2D6RZTSIUH9eOXZxX_4Au6nbMg.JPEG.naringring%2FIMG_4467.JPG&type=a340",
  //   },
  //   {
  //     name: "user1",
  //     img: "https://search.pstatic.net/common/?src=http%3A%2F%2Fblogfiles.naver.net%2FMjAyMzA4MTZfMTE5%2FMDAxNjkyMTY1ODEzNTgy.iJ0OgFhqLX7zgG2pRBgqrGrDKflLFVQafiuqVJYtoYcg.MueNDVOdzUEZjippuH2D6RZTSIUH9eOXZxX_4Au6nbMg.JPEG.naringring%2FIMG_4467.JPG&type=a340",
  //   },
  //   {
  //     name: "user2",
  //     img: "https://search.pstatic.net/common/?src=http%3A%2F%2Fblogfiles.naver.net%2FMjAyMzA4MTZfMTE5%2FMDAxNjkyMTY1ODEzNTgy.iJ0OgFhqLX7zgG2pRBgqrGrDKflLFVQafiuqVJYtoYcg.MueNDVOdzUEZjippuH2D6RZTSIUH9eOXZxX_4Au6nbMg.JPEG.naringring%2FIMG_4467.JPG&type=a340",
  //   },
  //   {
  //     name: "user3",
  //     img: "https://search.pstatic.net/common/?src=http%3A%2F%2Fblogfiles.naver.net%2FMjAyMzA4MTZfMTE5%2FMDAxNjkyMTY1ODEzNTgy.iJ0OgFhqLX7zgG2pRBgqrGrDKflLFVQafiuqVJYtoYcg.MueNDVOdzUEZjippuH2D6RZTSIUH9eOXZxX_4Au6nbMg.JPEG.naringring%2FIMG_4467.JPG&type=a340",
  //   },
  //   {
  //     name: "user4",
  //     img: "https://search.pstatic.net/common/?src=http%3A%2F%2Fblogfiles.naver.net%2FMjAyMzA4MTZfMTE5%2FMDAxNjkyMTY1ODEzNTgy.iJ0OgFhqLX7zgG2pRBgqrGrDKflLFVQafiuqVJYtoYcg.MueNDVOdzUEZjippuH2D6RZTSIUH9eOXZxX_4Au6nbMg.JPEG.naringring%2FIMG_4467.JPG&type=a340",
  //   },
  //   {
  //     name: "user5",
  //     img: "https://search.pstatic.net/common/?src=http%3A%2F%2Fblogfiles.naver.net%2FMjAyMzA4MTZfMTE5%2FMDAxNjkyMTY1ODEzNTgy.iJ0OgFhqLX7zgG2pRBgqrGrDKflLFVQafiuqVJYtoYcg.MueNDVOdzUEZjippuH2D6RZTSIUH9eOXZxX_4Au6nbMg.JPEG.naringring%2FIMG_4467.JPG&type=a340",
  //   },
  // ];

  return (
    <div className={styles.column}>
      <div className={styles.horizontal}>
        <div className={styles.title}>Online</div>
        <div className={styles.length}>{onlineUser.length}명</div>
      </div>
      <div className={styles.container}>
        {onlineUser.map((onlineuser, index) => {
          return <OnlineUser onlineUser={onlineuser} key={index} />
        })}
      </div>
    </div>
  );
}

export default Online;
