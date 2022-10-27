import React from "react";
import { useSelector } from "react-redux";
import { useNavigate } from "react-router-dom";

import LoginCheck from "../commons/login/LoginCheck";
import PlayerWithWeapon from "../components/game/PlayerWithWeapon";
import OrderPicker from "../components/game/OrderPicker";
import WeaponPicker from "../components/game/WeaponPicker";

import Grid from "@mui/material/Grid";

export default function Game() {
  // // 비 로그인 시 로그인 화면으로
  // const isLogin = LoginCheck();
  // const navigate = useNavigate();
  // React.useEffect(() => {
  //   if (!isLogin) {
  //     navigate("/login");
  //   }
  // }, []);
  const players = useSelector(state => state.players.value)

  function placePlayers(players) {
    let arr = [];
    if (players.peopleNum === 5) {
      arr.push(
        <Grid item xs={1}></Grid>
      )
    }
    for (let i = 1; i < players.peopleNum + 1; i++) {
      // A를 추후에 로그인한 유저의 팀과 비교하도록 변경
      arr.push(
        <Grid item xs={(players.peopleNum === 5) ? 2 : (12 / players.peopleNum)} key={players.teamA[`player${i}`].memberId}>
          {/* 추후에 isMe 삭제 */}
          <PlayerWithWeapon isMe={true} userName={players.teamA[`player${i}`].nickname} weapons={players.teamA[`player${i}`].weapons} />
        </Grid>
      );
    }
    return arr;
  }

  return (
    <div>
      <h1>진행 순서와 무기를 선택하세요</h1>
      <h3>100</h3>
      <div style={{ backgroundColor: "grey" }}>당신은 리더입니다.</div>
      <OrderPicker />
      <WeaponPicker></WeaponPicker>
      <Grid container>{placePlayers(players)}</Grid>
    </div>
  );
}
