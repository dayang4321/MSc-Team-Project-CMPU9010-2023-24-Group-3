import Head from "next/head";
import React, { useRef, useState } from "react";
import DefaultLayout from "../layouts/DefaultLayout";
import Button from "../components/UI/Button";
import axiosInit from "../services/axios";
import { useRouter } from "next/router";

export default function Home() {
  const [showMessage, setShowMessage] = useState(false);

  const router = useRouter();

  const [uploadProgress, setUploadProgress] = useState(0);

  const [isUploading, setIsUploading] = useState(false);

  const [docUploadError, setDocUploadError] = useState<null | string>("");

  const [dragActive, setDragActive] = useState(false);

  const handleUploadSuccess = () => {
    setShowMessage(true);
    setTimeout(() => {
      setShowMessage(false);
    }, 3000);
  };

  const [uploadedFiles, setUploadedFiles] = useState<
    { file: File | null; data: FileReader["result"] }[]
  >([]);

  console.log(uploadedFiles);

  const dragOverRef = useRef<HTMLLabelElement>(null);

  const onUploadConfirm = () => {
    setIsUploading(true);
    const formData = new FormData();

    formData.append("file", uploadedFiles[0].file);

    axiosInit
      .post("/uploadFile", formData, {
        headers: {
          "Content-Type": "multipart/form-data",
        },
        onUploadProgress: function (progressEvent) {
          let percentCompleted = Math.round(
            (progressEvent.loaded * 100) / progressEvent.total
          );
          setUploadProgress(percentCompleted);
        },
      })
      .then((res) => {
        console.log(res);

        //  router.push('/accessibility-review');
        router.push({
          pathname: "/accessibility-review",
          query: { doc_key: res.data.key },
        });
      })
      .catch((err) => {
        console.log(err);
        setDocUploadError(
          `An Error Occurred, Please try again ${
            err?.message ? `(Message: ${err?.message})` : ""
          }`
        );
      })
      .finally(() => {
        setIsUploading(false);
      });
  };

  const onDragOver = (e: React.DragEvent<HTMLLabelElement>) => {
    e.preventDefault();
    setDragActive(true);
    // dragOverRef.current?.classList.add("drag-over");
  };

  const onDragLeave = (e: React.DragEvent<HTMLLabelElement>) => {
    e.preventDefault();
    setDragActive(false);
    // dragOverRef.current?.classList.remove("drag-over");
  };

  const onFileDrop = (e: React.DragEvent<HTMLLabelElement>) => {
    e.preventDefault();
    setDragActive(false);
    // dragOverRef.current?.classList.remove("drag-over");

    if (e.dataTransfer.files.length > 0) {
      handleDroppedFiles(e.dataTransfer.files);
    }
  };

  const handleDroppedFiles = (files: FileList) => {
    if (uploadedFiles.length > 0) {
      return;
    }

    const fileListArr = Array.from(files);

    fileListArr.forEach((fileItem) => {
      const fileSizeKiloBytes = fileItem.size / 1024;
      const MAX_FILE_SIZE = 10240; // 10MB

      if (fileSizeKiloBytes < MAX_FILE_SIZE) {
        const reader = new FileReader();

        reader.onload = (readEvt) => {
          setUploadedFiles((prevFiles) => [
            ...prevFiles,
            { file: fileItem, data: readEvt?.target?.result || null },
          ]);
        };
        reader.readAsDataURL(fileItem);
      } else {
        // Handle file size too large error
      }
    });
  };

  const onFileUpload = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files && e.target.files.length > 0) {
      handleDroppedFiles(e.target.files);
    }
  };

  const onRetryUpload = () => {
    setDocUploadError("");
  };

  return (
    <DefaultLayout>
      <Head>
        <title>Accessibilator</title>
        <link rel="icon" href="/favicon.ico" />
      </Head>

      <main className="flex flex-col text-center justify-center items-center flex-1 bg-slate-50 text-gray-900">
        <h1 className="text-5xl max-w-4xl font-bold mb-11">
          Reading documents made more accessible😁
        </h1>

        <div className="bg-zinc-900 relative overflow-hidden rounded-2xl pt-12 px-8 pb-28 max-w-4xl text-white">
          <h2 className="text-4xl max-w-4xl font-semibold mb-4">
            Upload a document
          </h2>
          <p className="mb-5">
            Supported documents include Word(docx), Powerpoint(ppt), Excel
            (xlsx).
          </p>

          {uploadedFiles.length ? (
            <div className="bg-zinc-700 mt-14 text-zinc-100 text-base rounded-md text-left px-12 py-3">
              {isUploading ? (
                <p className="font-semibold text-2xl">
                  Uploading Document... {uploadProgress} %
                </p>
              ) : !!docUploadError ? (
                <div className="flex justify-between">
                  <p className="text-lg">{docUploadError}</p>
                </div>
              ) : (
                <>
                  <p className="mb-1">{uploadedFiles?.[0]?.file.name}</p>
                  <p>{niceBytes(uploadedFiles?.[0]?.file.size)}</p>
                </>
              )}
            </div>
          ) : (
            <label
              htmlFor="file-upload"
              className={`${
                dragActive
                  ? 'bg-zinc-900 border-zinc-50'
                  : 'bg-zinc-700 border-zinc-400'
              } mt-1 flex flex-col justify-center items-center rounded-md border-2 border-dashed  px-6 pt-5 pb-6`}
              ref={dragOverRef}
              onDragOver={onDragOver}
              onDragLeave={onDragLeave}
              onDrop={onFileDrop}
            >
              <div>
                <p className="mb-3">Select a file or drag and drop here</p>
              </div>
              <div className="space-y-1 text-center">
                <div className="flex text-sm text-gray-600">
                  <label
                    htmlFor="file-upload"
                    className="focus-within:ring-primary-500 hover:text-primary-500 relative cursor-pointer rounded-md bg-white font-medium text-primary-600 focus-within:outline-none focus-within:ring-2 focus-within:ring-offset-2"
                  >
                    <span className="btn inline-block border-white border-[1px] px-4 bg-gray-700 text-white ">
                      Select Document
                    </span>
                    <input
                      accept="doc,.docx,application/msword,application/vnd.openxmlformats-officedocument.wordprocessingml.document"
                      id="file-upload"
                      name="file-upload"
                      type="file"
                      className="sr-only"
                      onChange={onFileUpload}
                    />
                  </label>
                  <p className="pl-1"></p>
                </div>
                <p className="text-xs text-gray-50 pt-2">Max. size: 10MB</p>
              </div>
            </label>
          )}

          <div className="bg-zinc-800 text-right px-8 py-3 absolute bottom-0 left-0 w-full">
            <div
              aria-hidden={!uploadedFiles.length}
              className={uploadedFiles.length ? "visible" : "invisible"}
            >
              {!!docUploadError ? (
                <>
                  <Button
                    className="bg-zinc-50 text-base py-2 px-6 text-zinc-900 font-medium"
                    text={"Retry"}
                    onClick={() => {
                      onRetryUpload();
                    }}
                  />
                </>
              ) : (
                <>
                  <Button
                    variant="link"
                    className=" text-zinc-50 mr-10 py-2 px-6 text-base font-medium"
                    text={"Cancel"}
                    onClick={() => {
                      setUploadedFiles([]);
                    }}
                  />
                  <Button
                    className="bg-zinc-50 text-base py-2 px-6 text-zinc-900 font-medium"
                    text={'Continue to review'}
                    onClick={() => {
                      onUploadConfirm();
                    }}
                  />
                </>
              )}
            </div>
          </div>
        </div>

        {/* {showMessage && (
          // <PopupMessage type="success" content="File Uploaded successfully" />
        )} */}
      </main>
    </DefaultLayout>
  );
}

function niceBytes(x) {
  const units = ["Bytes", "KB", "MB", "GB", "TB", "PiB", "EiB", "ZiB", "YiB"];
  let l = 0,
    n = parseInt(x, 10) || 0;

  while (n >= 1024 && ++l) {
    n = n / 1024;
  }

  return n.toFixed(n < 10 && l > 0 ? 1 : 0) + " " + units[l];
}
