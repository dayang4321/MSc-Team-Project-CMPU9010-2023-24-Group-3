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

type Props = {};

const Reader = (props: Props) => {
  const router = useRouter();
  const { doc_id } = router.query;

  const [slideModalOpen, setSlideModalOpen] = useState(false);

  const [isDocDataLoading, setIsDocDataLoading] = useState(false);

  const [currDocData, setCurrDocData] = useState<DocumentData | null>(null);

  const fetchDocument = async (id: string) => {
    setIsDocDataLoading(true);
    try {
      const docRes = await axiosInit.get<DocumentData>(`/document/${id}`);
      return Promise.resolve(docRes.data);
    } catch (error) {
      console.log(error);
      // TODO: Toast error
      return Promise.reject(error);
    } finally {
      setIsDocDataLoading(false);
    }
  };

  useEffect(() => {
    fetchDocument(`${doc_id}`)
      .then((docRes) => {
        console.log(docRes);
        setCurrDocData(docRes);
      })
      .catch((err) => {
        console.log(err);
      });

    return () => {};
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [doc_id]);

  const docUri = currDocData?.versions.currentVersion.url;

  const [isModifyLoading, setIsModifyLoading] = useState(false);

  const onSaveConfig = (data: DocModifyParams) => {
    setIsModifyLoading(true);
    const docModParams: DocModifyParams = {
      fontType: data.fontType,
      fontSize: data.fontSize,
      lineSpacing: data.lineSpacing,
      fontColor: data.fontColor,
      backgroundColor: data.backgroundColor,
      characterSpacing: data.characterSpacing * 10,
      removeItalics: data.removeItalics,
      alignment: data.alignment,
      generateTOC: data.generateTOC,
    };

    axiosInit
      .get<DocumentData>('/modifyFile', {
        params: {
          filename: currDocData.documentKey,
          docID: currDocData.documentID,
          versionID: currDocData.versions.originalVersion.versionID,
          ...docModParams,
        },
      })
      .then((res) => {
        //  console.log(res);
        setCurrDocData(res.data);
        setSlideModalOpen(false);
      })
      .catch((err) => {
        console.log(err);
      })
      .finally(() => {
        setIsModifyLoading(false);
      });
  };

  const memoisedReader = useMemo(() => {
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
            backgroundColor: 'red',
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
          <Link
            href='/'
            className='btn-link inline-flex items-center px-2 py-2 text-base font-medium text-stone-700'
          >
            <span aria-hidden='true'>
              <HiArrowNarrowLeft className='mr-3 mt-[0.125rem] h-6 w-6' />
            </span>
            Back to homepage
          </Link>
        </div>

        <div>
          <Button
            role='navigation'
            variant='link'
            className=' mr-10 border border-yellow-900  px-6 py-2 text-base font-medium'
            text={'Upload New Document'}
            onClick={() => {
              router.push('/');
            }}
          />
          {docUri && (
            <Link
              href={docUri}
              className='btn-primary px-6 py-2 text-base'
              download
              target='_self'
            >
              Download
            </Link>
          )}
        </div>
      </nav>
      <main className='flex flex-1 bg-slate-50 py-16 pb-8 text-gray-900'>
        <div className='ml-24 flex flex-1 flex-col items-stretch border border-stone-800'>
          {!isDocDataLoading && memoisedReader}
        </div>
        <div className='flex flex-col self-stretch px-3'>
          <Button
            className='mt-10 inline-flex flex-col gap-y-3 rounded-3xl px-6 py-9 text-base font-medium'
            text={'Refine'}
            icon={<FaWandMagicSparkles className='h-7 w-7' />}
            onClick={() => {
              setSlideModalOpen(true);
            }}
          />
        </div>
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
            docConfigData={currDocData.documentConfig}
          />
        )}
      </SlideModal>
    </DefaultLayout>
  );
};

export default Reader;
