import { message } from "antd";

const PopupMessage = ({ type, content }) => {
  message[type](content);

  return null;
};

export default PopupMessage;
