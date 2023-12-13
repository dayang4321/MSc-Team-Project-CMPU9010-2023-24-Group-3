// Importing necessary components and hooks from React and other libraries
import React, { useEffect, useState } from 'react';
import DefaultLayout from '../../layouts/DefaultLayout';
import Head from 'next/head';
import Button from '../../components/UI/Button';
import IsProtectedRoute from '../../hoc/IsProtectedRoute';
import {
  HiOutlineDocumentText,
  HiOutlineDownload,
  HiOutlineTrash,
} from 'react-icons/hi';
import { formatDistanceStrict } from 'date-fns';
import axiosInit from '../../services/axios';
import { HashLoader } from 'react-spinners';
import Link from 'next/link';
import { ToastQueue } from '@react-spectrum/toast';
import { reportException } from '../../services/errorReporting';

/**
 * Defining the Loader component
 * It is displayed while the data is being loaded on the screen
 */
const loader = (
  <div className='flex h-96 w-full flex-col items-center justify-center'>
    <HashLoader
      loading={true}
      color='#451a03'
      size={'120px'}
      aria-description='Submitting Feedback'
    />
    <p className='mt-6 text-lg'>Loading Documents...</p>
  </div>
);

// Defining the main DocumentsPage functional component
const DocumentsPage = () => {
  // Initialize the state to store user documents and loading states
  const [userDocuments, setUserDocuments] = useState<
    {
      documentID: string;
      documentKey: string;
      version: string;
      createdDate: string;
    }[]
  >([]);

  const [isLoadingDocs, setIsLoadingDocs] = useState(true);

  const [isLoadingDeleteDocsObj, setIsLoadingDeleteDocObj] = useState<{
    [docID: string]: boolean;
  }>({});

  // Function to fetch user documents from the server
  const fetchUserDocuments = async () => {
    setIsLoadingDocs(true);
    try {
      const fetchUseDocsRes = await axiosInit.get<
        {
          documentID: string;
          documentKey: string;
          version: string;
          createdDate: string;
        }[]
      >('/api/user/documents');

      const currUserDocs = fetchUseDocsRes?.data || [];

      setIsLoadingDocs(false);
      return Promise.resolve(currUserDocs);
    } catch (error) {
      setIsLoadingDocs(false);
      return Promise.reject(error);
    }
  };

  // Define an Async function to delete a specific document
  const deleteDocument = async (id: string, name: string) => {
    setIsLoadingDeleteDocObj((s) => ({
      ...s,
      [id]: true,
    }));
    axiosInit
      .delete(`/api/user/documents/${id}`)
      .then((res) => {
        ToastQueue.neutral(`${name} deleted successfully`, {
          timeout: 1000,
        });

        setIsLoadingDeleteDocObj((s) => ({
          ...s,
          id: false,
        }));

        setUserDocuments((s) => {
          const docsCopy = [...s].filter((doc) => doc.documentID !== id);

          return docsCopy;
        });
      })
      .catch((err) => {
        ToastQueue.negative(
          `Failed to delete document ${
            err?.response?.data.detail || err?.message || ''
          }`,
          {
            timeout: 2000,
          }
        );
        reportException(err, {
          category: 'delete document',
          message: 'Failed to delete document',
          data: {
            origin: 'Past Document Screen',
          },
        });
      });
  };

  // useEffect to fetch documents on component mount
  useEffect(() => {
    fetchUserDocuments().then((value) => {
      setUserDocuments(value);
    });

    // Cleanup function if needed to get rid of any lingering side effects on the UI
    return () => {};
  }, []);

  // Render the UI for the Documents page
  return (
    <>
      <DefaultLayout>
        <Head>
          <title>Accessibilator | Your documents</title>
          <link rel='icon' href='/favicon.ico' />
        </Head>
        <main className='flex flex-1 flex-col justify-center px-36 py-8 text-center text-base text-gray-900'>
          <div className='col-span-3 flex flex-1 flex-col bg-stone-50 px-16 text-left'>
            <h2 className='text-3xl font-medium'>Your Past Documents</h2>
            <div className='mt-6 flex items-center gap-7 px-6 py-2 text-base font-semibold'>
              <div className='h-8 w-8'></div>
              <span className='flex-1'>Document Name</span>
              <span className='basis-32'>Uploaded</span>
              <span className='basis-[5rem] text-center'>Download</span>
              <span className='basis-[4.5rem] text-center'>Delete</span>
            </div>

            {isLoadingDocs ? (
              loader
            ) : !userDocuments?.length ? (
              <div className='flex flex-1 items-center justify-center text-gray-700'>
                <p>You have no recently modified documents</p>
              </div>
            ) : (
              <div className='mt-2 flex flex-col gap-y-4'>
                {userDocuments?.map((doc, idx) => {
                  const { documentKey, createdDate, documentID, version } = doc;
                  return (
                    <div
                      key={documentID}
                      className={`flex items-center gap-7 rounded-md border-2 border-red-950/20 px-6 py-3 ${
                        !!isLoadingDeleteDocsObj?.[documentID]
                          ? 'pointer-events-none bg-gray-400 opacity-80'
                          : 'pointer-events-auto'
                      }`}
                    >
                      <HiOutlineDocumentText className='h-8 w-8' />
                      <Link
                        href={{
                          pathname: '/reader',
                          query: {
                            doc_id: documentID,
                          },
                        }}
                        prefetch={false}
                        className='flex-1 text-lg'
                      >
                        {documentKey}
                      </Link>
                      <span className='capitalize-first basis-32'>
                        {formatDistanceStrict(
                          new Date(createdDate),
                          new Date(),
                          {
                            addSuffix: true,
                            roundingMethod: 'ceil',
                          }
                        )}
                      </span>
                      <div className='basis-[5rem] text-center'>
                        <Link
                          href={version}
                          className='btn-primary border-2 border-yellow-900 px-3 py-2'
                          prefetch={false}
                        >
                          <HiOutlineDownload className='h-6 w-6' />
                        </Link>
                      </div>
                      <div className='basis-[4.5rem] text-center'>
                        {!!isLoadingDeleteDocsObj?.[documentID] ? (
                          <HashLoader
                            loading={true}
                            color='#451a03'
                            size={'30px'}
                            aria-description='Submitting Feedback'
                          />
                        ) : (
                          <Button
                            variant='primary'
                            text=''
                            onClick={() => {
                              deleteDocument(documentID, documentKey);
                            }}
                            className='border-2 border-red-600  bg-red-400/20 px-3 py-2'
                            icon={
                              <HiOutlineTrash className='h-6 w-6 text-red-600' />
                            }
                          />
                        )}
                      </div>
                    </div>
                  );
                })}
              </div>
            )}
          </div>
        </main>
      </DefaultLayout>
    </>
  );
};

// Wrapping the component with a higher-order component for route protection
export default IsProtectedRoute(DocumentsPage);
