'''
Main method
'''

from readers import vcf_reader
from writers import mongo_db_writer
import os

def main():
    variants = []
    for file in os.listdir("Resources"):
        try:
            variants += vcf_reader.file_to_vcfs(os.path.join("Resources", file))
        except FileNotFoundError:
            print("File not found: " + file)
    print("Found : "+ str(len(variants))+" possible pathogenic variants from VCF file")
    mongo_db_writer.make_variants_database(variants)

main()