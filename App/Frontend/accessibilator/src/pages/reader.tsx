import React, { useEffect, useMemo, useState } from 'react';
import DefaultLayout from '../layouts/DefaultLayout';
import Head from 'next/head';
import Button from '../components/UI/Button';
import DocViewer, { DocViewerRenderers } from '@cyntler/react-doc-viewer';
import SlideModal from '../components/UI/SlideModal';
import { useRouter } from 'next/router';
import CustomisationPanel from '../components/CustomisationPanel/CustomisationPanel';
import Link from 'next/link';
import axiosInit from '../services/axios';
import { FaWandMagicSparkles } from 'react-icons/fa6';
import { HiArrowNarrowLeft } from 'react-icons/hi';
import { MdOutlineCompare } from 'react-icons/md';
import { reportException } from '../services/errorReporting';
import { ToastQueue } from '@react-spectrum/toast';

type Props = {};

const Reader = (props: Props) => {
  const router = useRouter();
  const { doc_id } = router.query;

  const [slideModalOpen, setSlideModalOpen] = useState(false);

  const [isComparingDocs, setIsComparingDocs] = useState(false);

  const [isDocDataLoading, setIsDocDataLoading] = useState(true);

  const [currDocData, setCurrDocData] = useState<DocumentData | null>(null);

  const fetchDocument = async (id: string) => {
    setIsDocDataLoading(true);
    try {
      const docRes = await axiosInit.get<DocumentData>(`/api/file/${id}`);
      return Promise.resolve(docRes.data);
    } catch (error) {
      console.log(error);
      return Promise.reject(error);
    } finally {
    }
  };

  useEffect(() => {
    setIsDocDataLoading(true);
    !!doc_id &&
      fetchDocument(`${doc_id}`)
        .then((docRes) => {
          setCurrDocData(docRes);
          setIsDocDataLoading(false);
        })
        .catch((err) => {
          setIsDocDataLoading(false);
          ToastQueue.negative(
            `An error occurred! ${
              err?.response?.data.detail || err?.message || ''
            }`,
            {
              timeout: 5000,
            }
          );
          reportException(err, {
            category: 'document_loading',
            message: 'Failed to load document',
            data: {
              origin: 'Reader Screen',
            },
          });
        });

    return () => {};
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [doc_id]);

  const docUri = currDocData?.versions.currentVersion.url;

  const [isModifyLoading, setIsModifyLoading] = useState(false);

  const onSaveConfig = (docParamData: DocModifyParams) => {
    setIsModifyLoading(true);
    const docModParams: DocModifyParams = {
      fontType: docParamData.fontType,
      fontSize: docParamData.fontSize,
      lineSpacing: docParamData.lineSpacing,
      fontColor: docParamData.fontColor,
      backgroundColor: docParamData.backgroundColor,
      characterSpacing: !!docParamData.characterSpacing
        ? docParamData.characterSpacing * 10
        : docParamData.characterSpacing,
      removeItalics: docParamData.removeItalics,
      alignment: docParamData.alignment,
      generateTOC: docParamData.generateTOC,
    };

    axiosInit
      .post<DocumentData>(
        '/api/file/modifyFile',
        { ...docModParams },
        {
          params: {
            filename: currDocData?.documentKey,
            docID: currDocData?.documentID,
            versionID: currDocData?.versions.originalVersion.versionID,
          },
        }
      )
      .then((res) => {
        //  console.log(res);
        setCurrDocData(res.data);
        setSlideModalOpen(false);
      })
      .catch((err) => {
        // console.log(err);
        ToastQueue.negative(
          `An error occurred! ${
            err?.response?.data.detail || err?.message || ''
          }`,
          {
            timeout: 5000,
          }
        );
        reportException(err, {
          category: 'modify',
          message: 'Failed to modify document',
          data: {
            origin: 'Reader Screen',
          },
        });
      })
      .finally(() => {
        setIsModifyLoading(false);
      });
  };

  const originalDocReader = useMemo(() => {
    return (
      currDocData?.versions.originalVersion && (
        <DocViewer
          requestHeaders={{
            'Access-Control-Allow-Origin': '*',
          }}
          prefetchMethod='GET'
          documents={[
            {
              uri: currDocData.versions.originalVersion.url,
            },
          ]}
          pluginRenderers={DocViewerRenderers}
          style={{
            flex: 1,
          }}
          config={{}}
          className='my-doc-viewer-style'
        />
      )
    );
  }, [currDocData?.versions.originalVersion]);

  const currentDocReader = useMemo(() => {
    return (
      docUri && (
        <DocViewer
          requestHeaders={{
            'Access-Control-Allow-Origin': '*',
          }}
          prefetchMethod='GET'
          documents={[
            {
              uri: docUri,
            },
          ]}
          pluginRenderers={DocViewerRenderers}
          style={{
            flex: 1,
          }}
          config={{}}
          className='my-doc-viewer-style'
        />
      )
    );
  }, [docUri]);

  return (
    <DefaultLayout title='Document Reader' variant='dark'>
      <Head>
        <title>Accessibilator | Document Reader</title>
        <link rel='icon' href='/favicon.ico' />
      </Head>
      <nav className='flex items-center justify-between border-b border-stone-800 px-16 py-3'>
        <div>
          {!!currDocData && !isComparingDocs && (
            <Link
              href={{
                pathname: '/accessibility-review',
                query: {
                  doc_key: currDocData?.documentKey,
                  doc_id: currDocData?.documentID,
                  version_id: currDocData?.versions.originalVersion.versionID,
                },
              }}
              className='btn-link inline-flex items-center px-2 py-2 text-base font-medium text-stone-700'
            >
              <span aria-hidden='true'>
                <HiArrowNarrowLeft className='mr-3 mt-[0.125rem] h-6 w-6' />
              </span>
              Back to review
            </Link>
          )}
        </div>

        <div className='flex items-center'>
          <Button
            role='navigation'
            variant={isComparingDocs ? 'primary' : 'link'}
            icon={
              isComparingDocs ? (
                <FaWandMagicSparkles className='h-5 w-5' />
              ) : (
                <MdOutlineCompare className='h-5 w-5' />
              )
            }
            className='mr-12 items-center border border-yellow-900  px-6 py-2 text-base font-medium'
            text={
              <span className='mb-[0.125rem] ml-1 inline-block'>
                {isComparingDocs ? 'Continue Customising' : 'Compare Original'}
              </span>
            }
            onClick={() => {
              setIsComparingDocs((s) => !s);
            }}
          />

          <div
            className={`transition-opacity ${
              isComparingDocs ? 'invisible' : 'visible'
            }`}
          >
            <Button
              role='navigation'
              variant='link'
              className='mr-10 border border-yellow-900  px-6 py-2 text-base font-medium'
              text={'Upload New Document'}
              onClick={() => {
                router.push('/');
              }}
            />
            {docUri && (
              <Link
                href={docUri}
                role='button'
                className='btn btn-primary px-6 py-2 text-base'
                download
                target='_self'
              >
                Download
              </Link>
            )}
          </div>
        </div>
      </nav>
      <main
        className={`flex flex-1 bg-slate-50 px-4 py-16 pb-8 text-gray-900 ${
          isComparingDocs ? 'pt-4' : 'pt-8'
        }`}
      >
        {isComparingDocs && (
          <div className='flex flex-1 flex-col items-stretch border border-stone-800  transition-all'>
            <h2 className='my-3 text-center text-lg font-semibold'>Original</h2>
            {!isDocDataLoading && originalDocReader}
          </div>
        )}
        <div
          className={`${
            isComparingDocs ? 'ml-12' : 'ml-24'
          } flex flex-1 flex-col items-stretch border border-stone-800`}
        >
          <h2 className='my-3 text-center text-lg font-semibold'>
            Modified{!isComparingDocs && ' Document'}
          </h2>
          {!isDocDataLoading && currentDocReader}
        </div>
        {!isComparingDocs && (
          <div className='flex flex-col self-stretch px-3 pr-0'>
            <Button
              className='mt-10 inline-flex flex-col gap-y-3 rounded-3xl px-6 py-9 text-base font-medium'
              text={'Refine'}
              icon={<FaWandMagicSparkles className='h-7 w-7' />}
              onClick={() => {
                setSlideModalOpen(true);
              }}
            />
          </div>
        )}
      </main>
      <SlideModal
        open={slideModalOpen}
        setOpen={setSlideModalOpen}
        title={'Customisation Panel'}
      >
        {currDocData && (
          <CustomisationPanel
            onConfigSave={onSaveConfig}
            configSaveLoading={isModifyLoading}
            customisationConfig={currDocData.documentConfig}
          />
        )}
      </SlideModal>
    </DefaultLayout>
  );
};

export default Reader;
