import React from 'react';
import { useSelector } from 'react-redux';
import { Modal, Box, Button, Grid, Input, InputLabel, MenuItem, FormControl, Select } from "@mui/material";
import ClearIcon from '@mui/icons-material/Clear';
import ArrowDropUpIcon from '@mui/icons-material/ArrowDropUp';
import ArrowDropDownIcon from '@mui/icons-material/ArrowDropDown';
import modalBackground from "../../_assets/room/enterRoom.png";
import ItemBox from "../ItemBox";
import { red, blue, white, black } from '../../_css/ReactCSSProperties';
import { modalStyle, inModalStyle, titleStyle, infoStyle, itemStyle, roomInfoTitleStyle, buttonStyle } from '../../_css/ModalCSSProperties';

// props - canEdit:boolean, roomData:redux, open:boolean_modalOpen, onClose:function_modalClose, onConfirm:function
export default function RoomSetting(props) {
	// websocket client
	const stompClient = useSelector((state) => state.websocket.stompClient);
	// 편집 가능 여부
	const [canEdit, setCanEdit] = React.useState(false);
	// 방 정보
	const [roomData, setRoomData] = React.useState(props.roomData);
	// 방제목
	const [title, setTitle] = React.useState(props.roomData.title);
	// 최대 유저수
	const [maxMember, setMaxMember] = React.useState(props.roomData.maxMember);
	// item count
	const [itemCount, setItemCount] = React.useState([props.roomData.sword, props.roomData.twin, props.roomData.shield, props.roomData.hand]);

	React.useEffect(() => {
		if (props.roomData) {
			console.log(props.roomData);
			setRoomData(props.roomData);
		}
	}, [props.roomData]);
	React.useEffect(() => {
		if (props.canEdit) {
			setCanEdit(props.canEdit);
		}
	}, [props.canEdit]);

	// 설정변경 취소
	const onModalClose = () => {
		setTitle(roomData.title);
		setMaxMember(roomData.maxMember);
		setItemCount([roomData.sword, roomData.twin, roomData.shield, roomData.hand]);
		props.onClose();
	}

	// 확인 버튼
	const onConfirm = () => {
		props.onConfirm(title, maxMember, itemCount);
		props.onClose();
	}

	const maxMemberChange = (event) => {
		setMaxMember(event.target.value);
	};

	//아이템 목록
	const items = ["검", "쌍검", "방패", "맨손"];

	//아이템 개수증가
	const itemCountUp = (index) => {
		const tempCount = [...itemCount];
		let tempSum = 0;
		tempCount.forEach(count => { tempSum += count; });
		console.log(tempSum);
		if (tempSum < maxMember) {
			tempCount[index]++;
		}
		else {
			alert("현재 무기 개수의 합이 최대입니다.");
		}
		setItemCount(tempCount);
	}

	// 아이템 개수감소
	const itemCountDown = (index) => {
		const tempCount = [...itemCount];
		if (tempCount[index] > 0) {
			tempCount[index]--;
		}
		setItemCount(tempCount);
	}

	const onChangeTitle = (e) => {
		setTitle(e.target.value);
	}

	return (
		<Modal
			open={props.open}
			// onClose={props.onClose}
			aria-labelledby="modal-modal-title"
			aria-describedby="modal-modal-description"
			sx={{ background: 'rgba(0, 0, 0, 0.7)' }}
		>
			<Box sx={{ ...modalStyle, backgroundImage: `url(${modalBackground})`, backgroundSize: 'cover' }}>
				<Box id="modal-modal-title">
					<Grid container alignItems={'center'} sx={{ p: 0, pt: 1, pb: 2 }}>
						<Grid item xs={11} sx={roomInfoTitleStyle}>게임방 정보</Grid>
						<Grid item xs={1}>
							<Button onClick={onModalClose} sx={{ color: "#DCD7C9" }}><ClearIcon /></Button>
						</Grid>
					</Grid>
				</Box>
				<Grid id="modal-modal-description" container sx={inModalStyle}>
					<Box sx={{ mt: 2, display: 'flex', flexDirection: 'column' }}>
						<Grid container item xs={12} sx={{ mt: 1, mb: 1 }}>
							<Grid item xs={2} sx={titleStyle}>방제목</Grid>
							{canEdit ?
								<Grid item xs={10} sx={{ pl: 4, pr: 4 }}><Input sx={{ ...infoStyle, color: black, pl: 3, width: "100%", background: white }} defaultValue={title} onChange={onChangeTitle} /></Grid>
								:
								<Grid item xs={10} sx={{ ...infoStyle, pl: 4, pr: 4 }}>{roomData.title}</Grid>
							}
						</Grid>
						<Grid container item xs={12} sx={{ mt: 1, mb: 1 }}>
							<Grid item xs={2} sx={titleStyle}>인원</Grid>
							{canEdit ?
								<Grid item xs={10} sx={{ pl: 4, pr: 4 }}>
									<Box sx={{ minWidth: 120 }}>
										<FormControl fullWidth>
											<InputLabel id="select-label">인원 수</InputLabel>
											<Select
												labelId="select-label"
												id="select"
												value={maxMember}
												defaultValue={maxMember}
												label="인원 수"
												onChange={maxMemberChange}
												sx={{ ...infoStyle, color: black, width: "100%", background: white }}
											>
												<MenuItem value={4} sx={{ fontSize: 18 }}>2 vs 2</MenuItem>
												<MenuItem value={6} sx={{ fontSize: 18 }}>3 vs 3</MenuItem>
												<MenuItem value={8} sx={{ fontSize: 18 }}>4 vs 4</MenuItem>
												<MenuItem value={10} sx={{ fontSize: 18 }}>5 vs 5</MenuItem>
											</Select>
										</FormControl>
									</Box>
								</Grid>
								:
								<Grid item xs={10} sx={{ ...infoStyle, pl: 4, pr: 4 }}>{roomData.currentMembers}/{roomData.maxMember}</Grid>
							}
						</Grid>
						<Grid container columns={24} item xs={24} sx={{ mt: 1, mb: 1 }}>
							<Grid container columns={24} item xs={4} sx={titleStyle} justifyContent="flex-end">아이템</Grid>
							{items.map((item, index) => (
								<Grid container columns={24} item xs={5} key={`item${item}`}>
									<Grid columns={12} item xs={12}><ItemBox size={150} textColor="#DCD7C9" buttonDisabled={true} /></Grid>
									{/* <Grid columns={12} item xs={12}><ItemBox text={item} size={190} textColor="#DCD7C9" buttonDisabled={true}/></Grid> */}
									<Grid columns={12} container item xs={12} alignItems="center">
										<Grid columns={12} item xs={7} sx={itemStyle}>{itemCount[index]}</Grid>
										{canEdit ?
											<Grid container columns={12} item xs={5} sx={{ mt: '-15px' }}>
												<Grid columns={12} item xs={12} alignItems="center">
													<Button onClick={(e) => { itemCountUp(index); }} sx={{ width: "100%", height: "100%", color: "#DCD7C9" }}><ArrowDropUpIcon /></Button>
												</Grid>
												<Grid columns={12} item xs={12} alignItems="center">
													<Button onClick={(e) => { itemCountDown(index); }} sx={{ width: "100%", height: "100%", color: "#DCD7C9" }}><ArrowDropDownIcon /></Button>
												</Grid>
											</Grid>
											:
											<div />
										}
									</Grid>
								</Grid>
							))}
						</Grid>
					</Box>
				</Grid>
				{canEdit ?
					<Grid container sx={{ mt: 4 }}>
						<Grid item xs={6} alignItems="center" sx={{ textAlign: "center" }}><Button variant="outlined" sx={buttonStyle} onClick={onConfirm}>확인</Button></Grid>
						<Grid item xs={6} alignItems="center" sx={{ textAlign: "center" }}><Button variant="outlined" sx={buttonStyle} onClick={onModalClose}>취소</Button></Grid>

						{/* <Grid item xs={6} alignItems="center" sx={{textAlign:"center"}}><Button variant="outlined" sx={{...buttonStyle, background: blue, color: "#DCD7C9"}} onClick={onRoomInfoChange}>확인</Button></Grid>
					<Grid item xs={6} alignItems="center" sx={{textAlign:"center"}}><Button variant="outlined" sx={{...buttonStyle, background: red, color: "#DCD7C9"}} onClick={onModalClose}>취소</Button></Grid> */}
					</Grid>
					:
					<Grid item xs={12} alignItems="center" sx={{ mt: 4, textAlign: "center" }}><Button variant="outlined" sx={buttonStyle} onClick={onConfirm}>입장</Button></Grid>
					// <Grid container sx={{mt:5}}><Button variant="outlined" onClick={enterRoom} sx={buttonStyle}>입장</Button></Grid>
				}
			</Box>
		</Modal>
	);
}