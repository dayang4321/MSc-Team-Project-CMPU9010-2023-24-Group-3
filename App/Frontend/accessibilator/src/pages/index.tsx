import Head from 'next/head';
import React, { useState } from 'react';
import FileUploadButton from '../components/FileUploadButton';
import PopupMessage from '../components/PopupMessage';
import DefaultLayout from '../layouts/DefaultLayout';

export default function Home() {
  const [showMessage, setShowMessage] = useState(false);

  const handleUploadSuccess = () => {
    setShowMessage(true);
    setTimeout(() => {
      setShowMessage(false);
    }, 3000);
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

        <div className="bg-gray-900 p-12 pb-16 max-w-4xl text-white">
          <h2 className="text-4xl max-w-4xl font-semibold mb-4">
            Upload a document
          </h2>
          <p className="mb-5">
            Supported documents include Word(docx), Powerpoint(ppt), Excel
            (xlsx).
          </p>
          <div>
            <p className="mb-3">Select a file or drag and drop here</p>
          </div>
          <FileUploadButton onUploadSuccess={handleUploadSuccess} />
        </div>

        {showMessage && (
          <PopupMessage type="success" content="File Uploaded successfully" />
        )}
      </main>
    </DefaultLayout>
  );
}
