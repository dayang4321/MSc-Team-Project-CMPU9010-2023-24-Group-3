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
        prefetchMethod="GET"
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
        className="my-doc-viewer-style"
      />
    );
  }, [docUri]);

  return (
    <DefaultLayout variant="dark">
      <Head>
        <title>Accessibilator</title>
        <link rel="icon" href="/favicon.ico" />
      </Head>
      <nav className="flex items-center px-20 py-3 border-b border-stone-800 justify-between">
        <div>
          <Button
            role="navigation"
            variant="link"
            className=" text-stone-700 py-2 px-6 text-base font-medium"
            text={'Back to homepage'}
            onClick={() => {
              router.push('/');
            }}
          />
        </div>

        <div>
          <Button
            role="navigation"
            variant="link"
            className=" text-stone-700 border border-stone-700 mr-10 py-2 px-6 text-base font-medium"
            text={'Upload New Document'}
            onClick={() => {
              router.push('/');
            }}
          />
          <Button
            className="bg-stone-700 text-base py-2 px-6 text-zinc-50 font-medium"
            text={'Export'}
            onClick={() => {}}
          />
        </div>
      </nav>
      <main className="flex py-16 pb-8 flex-1 bg-slate-50 text-gray-900">
        <div className="border border-stone-800 items-stretch flex-1 flex flex-col">
          {memoisedReader}
        </div>
        <div className="self-stretch flex flex-col px-3">
          <Button
            className="bg-stone-700 mt-10 inline-flex flex-col gap-y-3 rounded-3xl text-base py-9 px-6 text-zinc-50 font-medium"
            text={'Edit'}
            icon={<PencilIcon className="w-6 h-6" />}
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
