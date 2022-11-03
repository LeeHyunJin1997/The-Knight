import React from "react";
import MakeRoom from "../../functions/lobby/MakeRoom";
export default function TestMakeRoom(){
  const testRoomInfo = {
    title: "방제목",
    // maxUser: 4,
    // currentUser: 0,
    maxUser: 4,
    capacity: 4,
    participant: 0,
    sword: 1,
    twin: 1,
    shield: 1,
    hand: 1,
  }
  const onClick = ()=>{
    MakeRoom(testRoomInfo);
  }
  return (
    <button onClick={onClick}>makeRoom</button>
  );
}