import Head from 'next/head';
import React, { useRef, useState } from 'react';
import DefaultLayout from '../layouts/DefaultLayout';
import Button from '../components/UI/Button';
import axiosInit from '../services/axios';
import { useRouter } from 'next/router';
import { AxiosResponse } from 'axios';
import delay from 'lodash/delay';

export default function Home() {
  const router = useRouter();

  const [uploadProgress, setUploadProgress] = useState(0);

  const [isUploading, setIsUploading] = useState(false);

  const [docUploadError, setDocUploadError] = useState<null | string>('');

  const [dragActive, setDragActive] = useState(false);

  const [uploadedFiles, setUploadedFiles] = useState<
    { file: File | null; data: FileReader['result'] }[]
  >([]);

  const dragOverRef = useRef<HTMLLabelElement>(null);

  const inputRef = useRef<HTMLInputElement>(null);

  const onUploadConfirm = () => {
    setIsUploading(true);
    const formData = new FormData();

    formData.append('file', uploadedFiles[0].file);

    axiosInit
      .post('/uploadFile', formData, {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
        onUploadProgress: function (progressEvent) {
          let percentCompleted = Math.round(
            (progressEvent.loaded * 100) / progressEvent.total
          );
          setUploadProgress(percentCompleted);
        },
      })
      .then(
        (
          res: AxiosResponse<{
            documentID: string;
            url: string;
            key: string;
            versionID: string;
          }>
        ) => {
          console.log(res);
          setIsUploadRes(res.data);
          setIsUploading(false);

          delay(() => {
            router.push({
              pathname: '/accessibility-review',
              query: {
                doc_key: isUploadRes.key,
                doc_id: isUploadRes.documentID,
                version_id: isUploadRes.versionID,
              },
            });
          }, 1000);
        }
      )
      .catch((err) => {
        console.log(err);
        setDocUploadError(
          `An Error Occurred, Please try again ${
            err?.message ? `(Message: ${err?.message})` : ''
          }`
        );
        setIsUploading(false);
      });
  };

  const onDragOver = (e: React.DragEvent<HTMLLabelElement>) => {
    e.preventDefault();
    setDragActive(true);
  };

  const onDragLeave = (e: React.DragEvent<HTMLLabelElement>) => {
    e.preventDefault();
    setDragActive(false);
  };

  const onFileDrop = (e: React.DragEvent<HTMLLabelElement>) => {
    e.preventDefault();
    setDragActive(false);

    const allowedTypes = new Set(inputRef.current.accept.split(','));

    const isFileTypeAllowed = allowedTypes.has(e.dataTransfer.files[0].type);

    if (isFileTypeAllowed && e.dataTransfer.files.length === 1) {
      // stop event propagation
      e.preventDefault();
      handleDroppedFiles(e.dataTransfer.files);
      return;
    } else if (e.dataTransfer.files.length > 1) {
      // Handle too many files error
      //TODO: Toast message (One file at a time)
    } else if (!isFileTypeAllowed) {
      // Handle Unsupported file format
      //TODO: Toast message (Document format not supported)
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
    setDocUploadError('');
  };

  return (
    <DefaultLayout>
      <Head>
        <title>Accessibilator</title>
        <link rel='icon' href='/favicon.ico' />
      </Head>

      <main className='flex flex-1 flex-col items-center justify-center bg-slate-50 text-center text-gray-900'>
        <h1 className='mb-11 max-w-4xl text-5xl font-bold'>
          Reading documents made more accessible😁
        </h1>

        <div className='relative w-[40rem] max-w-full overflow-hidden rounded-2xl bg-zinc-900 px-8 pb-28 pt-12 text-white'>
          <h2 className='mb-4 max-w-4xl text-4xl font-semibold'>
            Upload a document
          </h2>
          <p className='mb-5'>Supported documents include Word(docx)</p>

          {uploadedFiles.length ? (
            <div className='mt-14 rounded-md bg-zinc-700 px-12 py-3 text-left text-base text-zinc-100'>
              {isUploading || uploadProgress === 100 ? (
                <p className='text-2xl font-semibold'>
                  {isUploading
                    ? `Uploading Document... ${uploadProgress}%`
                    : 'Upload Complete'}
                </p>
              ) : !!docUploadError ? (
                <div className='flex justify-between'>
                  <p className='text-lg'>{docUploadError}</p>
                </div>
              ) : (
                <>
                  <p className='mb-1'>{uploadedFiles?.[0]?.file.name}</p>
                  <p>{niceBytes(uploadedFiles?.[0]?.file.size)}</p>
                </>
              )}
            </div>
          ) : (
            <label
              htmlFor='file-upload'
              className={`${
                dragActive
                  ? 'border-zinc-50 bg-zinc-900'
                  : 'border-zinc-400 bg-zinc-700'
              } mt-1 flex flex-col items-center justify-center rounded-md border-2 border-dashed  px-6 pb-6 pt-5`}
              ref={dragOverRef}
              onDragOver={onDragOver}
              onDragLeave={onDragLeave}
              onDrop={onFileDrop}
            >
              <div>
                <p className='mb-3'>Select a file or drag and drop here</p>
              </div>
              <div className='space-y-1 text-center'>
                <div className='flex text-sm text-gray-600'>
                  <label
                    htmlFor='file-upload'
                    className='relative cursor-pointer rounded-md bg-white font-medium text-primary-600 focus-within:outline-none focus-within:ring-2 focus-within:ring-primary-500 focus-within:ring-offset-2 hover:text-primary-500'
                  >
                    <span className='btn inline-block border-[1px] border-white bg-gray-700 px-4 text-white '>
                      Select Document
                    </span>
                    <input
                      accept='doc,.docx,application/msword,application/vnd.openxmlformats-officedocument.wordprocessingml.document'
                      id='file-upload'
                      name='file-upload'
                      type='file'
                      className='sr-only'
                      ref={inputRef}
                      onChange={onFileUpload}
                    />
                  </label>
                  <p className='pl-1'></p>
                </div>
                <p className='pt-2 text-xs text-gray-50'>Max. size: 10MB</p>
              </div>
            </label>
          )}

          <div className='absolute bottom-0 left-0 w-full bg-zinc-800 px-8 py-3 text-right'>
            <div
              aria-hidden={!uploadedFiles.length}
              className={uploadedFiles.length ? 'visible' : 'invisible'}
            >
              {!!docUploadError ? (
                <>
                  <Button
                    className='bg-zinc-50 px-6 py-2 text-base font-medium text-zinc-900'
                    text={'Retry'}
                    onClick={() => {
                      onRetryUpload();
                    }}
                  />
                </>
              ) : (
                <>
                  <Button
                    variant='link'
                    className=' mr-10 px-6 py-2 text-base font-medium text-zinc-50'
                    text={'Cancel'}
                    onClick={() => {
                      setUploadedFiles([]);
                    }}
                  />
                  <Button
                    className='bg-zinc-50 px-6 py-2 text-base font-medium text-zinc-900'
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
