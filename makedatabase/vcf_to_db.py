"""
VCF to MongoDB

This script uses the exomes VCF file form gnomAD: https://gnomad.broadinstitute.org/downloads
to filter out benign variants and add the possible pathogenic variants to a Mongo Database
A variants is considered benign when it occurs in  more than 1 percent of the population
and is not present in patients with cancer (AF > 1 & non_cancer_AF = 0)
"""

from readers import vcf_reader
from writers import mongo_db_writer
import os
import logging

def main():
    logging.basicConfig(format='%(asctime)s %(levelname)s:%(message)s',level=logging.INFO)
    variants = []
    for file in os.listdir("Resources"):
        try:
            new_variants = vcf_reader.file_to_vcfs(os.path.join("Resources", file))
            logging.info(str(len(new_variants))+" new possible pathogenic variants in file: "+file)
            variants += new_variants
        except FileNotFoundError:
            print("File not found: " + file)
    logging.info("Found : "+ str(len(variants))+" possible pathogenic variants from VCF files")
    mongo_db_writer.make_variants_database(variants)

main()