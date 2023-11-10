import React, { useMemo, useState } from 'react';
import DefaultLayout from '../layouts/DefaultLayout';
import Head from 'next/head';
import Button from '../components/UI/Button';
import DocViewer, { DocViewerRenderers } from '@cyntler/react-doc-viewer';
import SlideModal from '../components/UI/SlideModal';
import { PencilIcon } from '@heroicons/react/24/outline';
import { useRouter } from 'next/router';

type Props = {};

const reader = (props: Props) => {
  const [slideModalOpen, setSlideModalOpen] = useState(false);

  const router = useRouter();
  const { doc_url } = router.query;

  const docUri = Array.isArray(doc_url) ? doc_url[0] : doc_url;

  const memoisedReader = useMemo(() => {
    return (
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
    );
  }, [docUri]);

  return (
    <DefaultLayout variant='dark'>
      <Head>
        <title>Accessibilator</title>
        <link rel='icon' href='/favicon.ico' />
      </Head>
      <nav className='flex items-center justify-between border-b border-stone-800 px-20 py-3'>
        <div>
          <Button
            role='navigation'
            variant='link'
            className=' px-6 py-2 text-base font-medium text-stone-700'
            text={'Back to homepage'}
            onClick={() => {
              router.push('/');
            }}
          />
        </div>

        <div>
          <Button
            role='navigation'
            variant='link'
            className=' mr-10 border border-stone-700 px-6 py-2 text-base font-medium text-stone-700'
            text={'Upload New Document'}
            onClick={() => {
              router.push('/');
            }}
          />
          <Button
            className='bg-stone-700 px-6 py-2 text-base font-medium text-zinc-50'
            text={'Export'}
            onClick={() => {}}
          />
        </div>
      </nav>
      <main className='flex flex-1 bg-slate-50 py-16 pb-8 text-gray-900'>
        <div className='flex flex-1 flex-col items-stretch border border-stone-800'>
          {memoisedReader}
        </div>
        <div className='flex flex-col self-stretch px-3'>
          <Button
            className='mt-10 inline-flex flex-col gap-y-3 rounded-3xl bg-stone-700 px-6 py-9 text-base font-medium text-zinc-50'
            text={'Edit'}
            icon={<PencilIcon className='h-6 w-6' />}
            onClick={() => {
              setSlideModalOpen(true);
            }}
          />
        </div>
      </main>
      <SlideModal
        open={slideModalOpen}
        setOpen={setSlideModalOpen}
        title={'Edit Document'}
      >
        <div>Customisation Panel</div>
      </SlideModal>
    </DefaultLayout>
  );
};

export default reader;
