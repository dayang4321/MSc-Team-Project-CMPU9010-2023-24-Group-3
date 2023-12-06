import React, { useContext, useEffect, useState } from 'react';
import DefaultLayout from '../../layouts/DefaultLayout';
import Head from 'next/head';
import Button from '../../components/UI/Button';
import { AuthContext } from '../../contexts/AuthContext';
import IsProtectedRoute from '../../hoc/IsProtectedRoute';
import {
  HiOutlineDocumentText,
  HiOutlineDownload,
  HiOutlineTrash,
} from 'react-icons/hi';
import { formatDistanceStrict } from 'date-fns';
import axiosInit from '../../services/axios';

const defaultSettings: DocModifyParams = {
  fontType: 'arial',
  fontSize: 12,
  lineSpacing: 1.5,
  fontColor: '000000',
  backgroundColor: 'FFFFFF',
  characterSpacing: 2.5,
  removeItalics: true,
  alignment: 'LEFT',
  generateTOC: false,
};

const DocumentsPage = () => {
  const { user, logout } = useContext(AuthContext);

  const [userDocuments, setUserDocuments] = useState<Required<
    User['userDocuments']
  > | null>(null);

  const [isLoadingDocs, setIsLoadingDocs] = useState(true);

  const fetchUserDocuments = async () => {
    setIsLoadingDocs(true);
    try {
      const fetchUserRes = await axiosInit.get<{ user: User | null }>(
        '/api/user/me'
      );

      const currUserDocs = fetchUserRes?.data?.user?.userDocuments || [];

      setIsLoadingDocs(false);
      return Promise.resolve(currUserDocs);
    } catch (error) {
      setIsLoadingDocs(false);
      return Promise.reject(error);
    }
  };

  useEffect(() => {
    fetchUserDocuments().then((value) => {
      setUserDocuments(value);
    });

    return () => {};
  }, []);

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
              <span className='basis-28'>Modified</span>
              <span className='basis-[5rem] text-center'>Download</span>
              <span className='basis-[4.5rem] text-center'>Delete</span>
            </div>

            <div className='mt-2 flex flex-col gap-y-4'>
              {userDocuments?.map((doc, idx) => {
                const { documentKey, createdDate, documentID } = doc;
                return (
                  <div
                    key={documentID}
                    className='flex items-center gap-7 rounded-md border-2 border-red-950/20 px-6 py-3'
                  >
                    <HiOutlineDocumentText className='h-8 w-8' />
                    <span className='flex-1 text-lg'>{documentKey}</span>
                    <span className='capitalize-first basis-28'>
                      {formatDistanceStrict(new Date(createdDate), new Date(), {
                        addSuffix: true,
                        roundingMethod: 'ceil',
                      })}
                    </span>
                    <div className='basis-[5rem] text-center'>
                      <Button
                        text=''
                        className='border-2 border-yellow-900 px-3 py-2'
                        icon={<HiOutlineDownload className='h-6 w-6' />}
                      />
                    </div>
                    <div className='basis-[4.5rem] text-center'>
                      <Button
                        variant='primary'
                        text=''
                        className='border-2 border-red-600  bg-red-400/20 px-3 py-2'
                        icon={
                          <HiOutlineTrash className='h-6 w-6 text-red-600' />
                        }
                      />
                    </div>
                  </div>
                );
              })}
            </div>
          </div>
        </main>
      </DefaultLayout>
    </>
  );
};

export default IsProtectedRoute(DocumentsPage);
