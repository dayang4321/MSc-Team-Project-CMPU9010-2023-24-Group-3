import Head from 'next/head';
import React, { useState } from 'react';
import DefaultLayout from '../layouts/DefaultLayout';
import Button from '../components/UI/Button';

export default function Home() {
  const [showMessage, setShowMessage] = useState(false);

  const handleUploadSuccess = () => {
    setShowMessage(true);
    setTimeout(() => {
      setShowMessage(false);
    }, 3000);
  };

  const [uploadedFiles, setUploadedFiles] = useState<
    { file: File | null; data: FileReader['result'] }[]
  >([]);

  console.log(uploadedFiles);

  const onFileUpload = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (uploadedFiles?.length > 1) {
      //  toast.error("We don't need more than 1 file");
      return;
    }
    const fileList = e.target.files;
    var fileListArr = !!fileList ? Array.from(fileList) : [];

    fileListArr.forEach((fileItem) => {
      const fileSizeKiloBytes = fileItem.size / 1024;
      const MAX_FILE_SIZE = 5120; // 5MB

      if (fileSizeKiloBytes < MAX_FILE_SIZE) {
        const reader = new FileReader();
        // eslint-disable-next-line no-loop-func

        reader.onload = (readEvt) => {
          setUploadedFiles((s) => [
            ...s,
            { file: fileItem, data: readEvt?.target?.result || null },
          ]);
        };
        reader.readAsDataURL(fileItem);
      } else {
        //  toast.error('File is too large (max. 5MB)');
      }
    });
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
              <p className="mb-1">{uploadedFiles?.[0]?.file.name}</p>
              <p>{niceBytes(uploadedFiles?.[0]?.file.size)}</p>
            </div>
          ) : (
            <label
              htmlFor="file-upload"
              className="mt-1 flex flex-col justify-center items-center rounded-md border-2 border-dashed border-zinc-400 px-6 pt-5 pb-6"
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

          {/* <Button text={'Select Document'} onClick={() => {}} /> */}
          {/* <FileUploadButton onUploadSuccess={handleUploadSuccess} /> */}

          <div className="bg-zinc-800 text-right px-8 py-3 absolute bottom-0 left-0 w-full">
            <div
              aria-hidden={!uploadedFiles.length}
              className={uploadedFiles.length ? 'visible' : 'invisible'}
            >
              <Button
                variant="link"
                className=" text-zinc-50 mr-10 py-2 px-6 text-base font-medium"
                text={'Cancel'}
                onClick={() => {
                  setUploadedFiles([]);
                }}
              />
              <Button
                className="bg-zinc-50 text-base py-2 px-6 text-zinc-900 font-medium"
                text={'Scan for Accessibility'}
                onClick={() => {}}
              />
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
  const units = ['Bytes', 'KB', 'MB', 'GB', 'TB', 'PiB', 'EiB', 'ZiB', 'YiB'];
  let l = 0,
    n = parseInt(x, 10) || 0;

  while (n >= 1024 && ++l) {
    n = n / 1024;
  }

  return n.toFixed(n < 10 && l > 0 ? 1 : 0) + ' ' + units[l];
}
