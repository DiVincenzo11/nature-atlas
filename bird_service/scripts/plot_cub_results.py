"""Render statistics only; never embed CUB photographs in release figures."""
import argparse
import csv
import json
from pathlib import Path
import matplotlib
matplotlib.use('Agg')
import matplotlib.pyplot as plt


def main():
    p=argparse.ArgumentParser();p.add_argument('--artifacts',type=Path,required=True);a=p.parse_args()
    read=lambda name:json.loads((a.artifacts/name).read_text(encoding='utf-8'))
    results=read('summary.json')['results']
    fig,axes=plt.subplots(1,3,figsize=(14,4.4),layout='constrained')
    for name,title in [('frozen_head','Frozen features'),('finetuned','Staged fine-tuning')]:
        history=read(name+'/history.json')
        axes[0].plot([h['epoch'] for h in history],[h['validation']['macro_f1'] for h in history],marker='.',label=title)
    axes[0].set(xlabel='Epoch',ylabel='Validation macro-F1',title='Checkpoint selection: validation only')
    axes[0].legend(fontsize=9);axes[0].grid(alpha=.25)
    names=['Frozen features','Fine-tuned'];values=[100*results[n]['accuracy'] for n in ['frozen_head','finetuned']]
    axes[1].bar(names,values,color=['#78909c','#258b73'])
    for i,v in enumerate(values):axes[1].text(i,v+1,f'{v:.2f}%',ha='center')
    axes[1].set(ylim=(0,100),ylabel='Official test top-1 (%)',title='GT crop species classification')
    rates=results['pipeline']['rates']
    values=[100*rates['any_box_subject_recall_iou50'],100*rates['primary_box_subject_recall_iou50'],100*rates['localized_accepted_correct_over_all_images']]
    axes[2].bar(['Any box\nIoU >= .5','Primary box\nIoU >= .5','Primary box +\naccepted species'],values,color=['#78909c','#78909c','#258b73'])
    for i,v in enumerate(values):axes[2].text(i,v+1,f'{v:.2f}%',ha='center')
    axes[2].set(ylim=(0,100),ylabel='Official test images (%)',title='YOLOv8n + fine-tuned classifier')
    fig.suptitle('CUB-200-2011 | 200 classes | 5,794 official test images | seed 42',fontsize=13)
    fig.text(.5,-.03,'ImageNet pretraining may overlap CUB test images. Single run; not original competition results or detection mAP.',ha='center',fontsize=9)
    fig.savefig(a.artifacts/'results.png',dpi=170,bbox_inches='tight');plt.close(fig)
    report=read('finetuned-test.json');matrix=report['confusion_matrix'];labels=report['labels']
    with (a.artifacts/'finetuned-confusion.csv').open('w',newline='',encoding='utf-8') as stream:
        writer=csv.writer(stream);writer.writerow(['target / prediction']+labels)
        writer.writerows([label]+row for label,row in zip(labels,matrix))
    pairs=sorted([(matrix[i][j],labels[i],labels[j]) for i in range(len(labels)) for j in range(len(labels)) if i!=j and matrix[i][j]],reverse=True)
    with (a.artifacts/'most-confused.csv').open('w',newline='',encoding='utf-8') as stream:
        writer=csv.writer(stream);writer.writerow(['count','target','prediction']);writer.writerows(pairs)


if __name__=='__main__':main()
