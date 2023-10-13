import { Upload, Button, message } from "antd";
import { UploadOutlined } from "@ant-design/icons";

const FileUploadButton = ({onUploadSuccess}) => {
  const checkFileType = (file) => {
    const allowedFileTypes = [
      ".txt",
      ".doc",
      ".docx",
      ".rtf", // Text-based files
      ".ppt",
      ".pptx", // Presentation files
      ".csv",
      ".xls",
      ".xlsx", // Spreadsheet files
    ];

    const fileType = file.name
      .slice(((file.name.lastIndexOf(".") - 1) >>> 0) + 2)
      .toLowerCase();

    if (allowedFileTypes.includes(`.${fileType}`)) {
      return true;
    } else {
      message.error("Unsupported file type. Please select a valid file type.");
      return false;
    }
  };

  const customRequest = ({ file, onSuccess, onError }) => {
    // Implement the file upload logic using Axios or any other library here.

    // Adding a short delay to simulate file upload.
    setTimeout(() => {
      onSuccess("ok");
      onUploadSuccess(); 
    }, 1000);
  };

  return (
    <Upload
      beforeUpload={checkFileType}
      customRequest={customRequest}
      progress={true}
      showUploadList={true}
    >
      <Button
        icon={<UploadOutlined>Select File</UploadOutlined>}
        title="Select files to upload"
      ></Button>
    </Upload>
  );
};

export default FileUploadButton;
